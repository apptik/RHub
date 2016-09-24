package io.apptik.rxhub;


import com.jakewharton.rxrelay.BehaviorRelay;
import com.jakewharton.rxrelay.PublishRelay;
import com.jakewharton.rxrelay.Relay;
import com.jakewharton.rxrelay.ReplayRelay;
import com.jakewharton.rxrelay.SerializedRelay;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

import static io.apptik.rxhub.RxJava1Hub.RxJava1ProxyType.BehaviorRelayProxy;
import static io.apptik.rxhub.RxJava1Hub.RxJava1ProxyType.BehaviorSubjectProxy;
import static io.apptik.rxhub.RxJava1Hub.RxJava1ProxyType.PublishRelayProxy;
import static io.apptik.rxhub.RxJava1Hub.RxJava1ProxyType.PublishSubjectProxy;
import static io.apptik.rxhub.RxJava1Hub.RxJava1ProxyType.ReplayRelayProxy;
import static io.apptik.rxhub.RxJava1Hub.RxJava1ProxyType.ReplaySubjectProxy;


/**
 * Base implementation of {@link RxJava1Hub}
 * Essentially this is a collection of {@link Observable} proxies which can also subscribe to other
 * Observables and pass events to their Subscribers
 * <p/>
 * Proxys can be either {@link Subject} or {@link Relay}. Proxys are identified by their Tags.
 * Proxys subscribes to Observables however each subscription created is
 * per {@link Source}. A Source is identified by Observable and a Tag.
 * For example when Observable A is added with Tag T1 and Tag T2. Two proxies are created receiving
 * the same events. Each of those proxies can be used and unsubscribed from Observable A
 * independently.
 * <p/>
 * Observers subscribe to a Proxy. Observers does not need to know about the source of the Events
 * i.e the Observers that the Proxys is subscribed to.
 * <p/>
 * To fetch the Proxy to subscribe to {@link AbstractRxJava1Hub#getObservable(Object)} must be
 * called.
 * <p/>
 * Non-Rx code can also call {@link AbstractRxJava1Hub#emit(Object, Object)} to manually emit Events
 * through specific Proxy.
 */
public abstract class AbstractRxJava1Hub implements RxJava1Hub {

    private final Map<Object, Observable> proxyMap = new ConcurrentHashMap<>();
    private final Map<Source, Subscription> subscriptionMap = new ConcurrentHashMap<>();

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    public final void addObservable(final Object tag, final Observable observable) {
        if (getProxyType(tag) == RxJava1ProxyType.ObservableRefProxy) {
            proxyMap.put(tag, observable);
        } else {
            Observable proxy = proxyMap.get(tag);
            if (proxy == null) {
                proxy = createProxy(tag);
            }
            ConnectableObservable obs = observable.publish();
            if (Action1.class.isAssignableFrom(proxy.getClass())) {
                obs.subscribe((Action1) proxy);
            } else if (Observer.class.isAssignableFrom(proxy.getClass())) {
                obs.subscribe((Observer) proxy);
            } else {
                //should not happen
                throw new IllegalStateException(String.format(Locale.ENGLISH,
                        "Proxy(%s) type(%s) is not supported! Do we have an alien injection?",
                        tag, observable.getClass()));
            }
            obs.connect(new Action1<Subscription>() {
                @Override
                public void call(Subscription subscription) {
                    subscriptions.add(subscription);
                    subscriptionMap.put(new Source(observable, tag), subscription);
                }
            });
        }
    }

    @Override
    public final void removeObservable(Object tag, Observable observable) {
        if (getProxyType(tag) == RxJava1ProxyType.ObservableRefProxy) {
            proxyMap.remove(tag);
        } else {
            Source s = new Source(observable, tag);
            subscriptions.remove(subscriptionMap.get(s));
            subscriptionMap.remove(s);
        }
    }

    @Override
    public final Observable getObservable(Object tag) {
        //make sure we expose it asObservable hide proxy's identity
        return getProxyInternal(tag).asObservable();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> Observable<T> getObservable(Object tag, final Class<T> filterClass) {
        return getObservable(tag).filter(new Func1<Object, Boolean>() {
            @Override
            public Boolean call(Object obj) {
                return filterClass.isAssignableFrom(obj.getClass());
            }
        });
    }


    private Observable getProxyInternal(Object tag) {
        Observable res = proxyMap.get(tag);
        if (res == null) {
            res = createProxy(tag);
        }
        return res;
    }

    private Observable createProxy(Object tag) {
        Observable res;
        RxJava1ProxyType nt = getProxyType(tag);
        switch (nt) {
            case BehaviorSubjectProxy:
                res = BehaviorSubject.create();
                break;
            case PublishSubjectProxy:
                res = PublishSubject.create();
                break;
            case ReplaySubjectProxy:
                res = ReplaySubject.create();
                break;
            case BehaviorRelayProxy:
                res = BehaviorRelay.create();
                break;
            case PublishRelayProxy:
                res = PublishRelay.create();
                break;
            case ReplayRelayProxy:
                res = ReplayRelay.create();
                break;
            case ObservableRefProxy:
                throw new IllegalStateException("Cannot create ObservableRefProxy, " +
                        "it must be added before.");
                //should not happen;
            default:
                throw new IllegalStateException("Unknown ProxyType");
        }

        if (isProxyThreadsafe(tag)) {
            switch (nt) {
                case BehaviorSubjectProxy:
                case PublishSubjectProxy:
                case ReplaySubjectProxy:
                    res = new SerializedSubject((Subject) res);
                    break;
                case BehaviorRelayProxy:
                case PublishRelayProxy:
                case ReplayRelayProxy:
                    res = new SerializedRelay((Relay) res);
                    break;
            }
        }
        proxyMap.put(tag, res);
        return res;
    }

    @Override
    public final void emit(Object tag, Object event) {
        if (!canTriggerEmit(tag)) {
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Emitting events on Tag(%s) not allowed.", tag));
        }
        RxJava1ProxyType proxyType = getProxyType(tag);
        if (proxyType == RxJava1ProxyType.ObservableRefProxy) {
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Emitting event not possible. Tag(%s) represents immutable stream.", tag));
        } else {
            Observable proxy = getProxyInternal(tag);
            if (proxyType == BehaviorRelayProxy ||
                    proxyType == PublishRelayProxy ||
                    proxyType == ReplayRelayProxy) {
                ((Action1) proxy).call(event);
            } else if (proxyType == BehaviorSubjectProxy ||
                    proxyType == PublishSubjectProxy ||
                    proxyType == ReplaySubjectProxy) {
                ((Observer) proxy).onNext(event);
            } else {
                //should not happen
                throw new IllegalStateException(String.format(Locale.ENGLISH,
                        "Proxy(%s) type(%s) is not supported! Do we have an alien injection?",
                        tag, proxy.getClass()));
            }
        }
    }

    @Override
    public final void clearObservables() {
        subscriptions.clear();
        subscriptionMap.clear();
    }

    @Override
    public void resetObsProxy(Object tag) {
        //temp impl will move to proxy soon
        if (getProxyType(tag) == RxJava1ProxyType.ObservableRefProxy) {
            proxyMap.remove(tag);
        } else {
            Subject subject = (Subject) proxyMap.get(tag);
            if (subject != null) {
                subject.onCompleted();
            }
            proxyMap.remove(tag);
            Iterator<Map.Entry<Source, Subscription>> iterator = subscriptionMap.entrySet()
                    .iterator();
            while (iterator.hasNext()) {
                Map.Entry<Source, Subscription> entry = iterator.next();
                if (entry.getKey().tag.equals(tag)) {
                    subscriptions.remove(entry.getValue());
                    iterator.remove();
                }
            }
        }
    }
}
