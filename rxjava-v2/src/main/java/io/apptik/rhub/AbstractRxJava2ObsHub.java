package io.apptik.rhub;


import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.apptik.roxy.Removable;
import io.apptik.roxy.SubjProxy;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;

import static io.apptik.rhub.RxJava2ObsHub.RxJava2ObsProxyType.ObservableRefProxy;
import static io.apptik.roxy.Roxy.TePolicy.PASS;
import static io.apptik.roxy.Roxy.TePolicy.WRAP;


/**
 * Base implementation of {@link RxJava2ObsHub}
 * Essentially this is a collection of {@link Observable} ND {@link Publisher} proxies which can
 * also
 * subscribe to other Observables and pass events to their Subscribers
 * <p/>
 * Proxies can be either {@link Subject} or {@link Processor}. Proxies are identified by their Tags.
 * Proxies subscribes to Observables however each subscription created is
 * per 'Source'. A 'Source' is a combination of an Observable and a Tag.
 * For example when Observable A is added with Tag T1 and Tag T2. Two proxies are created receiving
 * the same events. Each of those proxies can be used and unsubscribed from Observable A
 * independently.
 * <p/>
 * Observers subscribe to a RSProxy. Observers does not need to know about the source of the Events
 * i.e the Observers that the Proxies is subscribed to.
 * <p/>
 * To fetch the RSProxy to subscribe to {@link AbstractRxJava2ObsHub#getPub(Object)} must be
 * called.
 * <p/>
 * Non-Rx code can also call {@link AbstractRxJava2ObsHub#emit(Object, Object)} to manually emit
 * Events
 * through specific RSProxy.
 */
public abstract class AbstractRxJava2ObsHub implements RxJava2ObsHub {

    private final Map<Object, SubjProxy> proxyObsMap = new ConcurrentHashMap<>();
    private final Map<Object, Observable> directObsMap = new ConcurrentHashMap<>();

    @Override
    public final Removable addUpstream(final Object tag, final Observable observable) {
        if (getProxyType(tag) == ObservableRefProxy) {
            directObsMap.put(tag, observable);
        } else {
            getObservableProxyInternal(tag);
            proxyObsMap.get(tag).addUpstream(observable);
        }
        return new Removable() {
            @Override
            public void remove() {
                AbstractRxJava2ObsHub.this.removeUpstream(tag, observable);
            }
        };
    }

    @Override
    public final void removeUpstream(Object tag, Observable observable) {
        if (getProxyType(tag) == ObservableRefProxy) {
            directObsMap.remove(tag);
        } else {
            SubjProxy proxy = proxyObsMap.get(tag);
            if (proxy != null) {
                proxy.removeUpstream(observable);
            }
        }
    }

    @Override
    public final Observable getPub(Object tag) {
        Observable res = getObservableProxyInternal(tag);
        if (getProxyType(tag) == ObservableRefProxy) {
            return res;
        } else {
            //make sure we expose it as Observable hide proxy's identity
            return res.hide();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> Observable<T> getPub(Object tag, final Class<T> filterClass) {
        return getObservableProxyInternal(tag).filter(new Predicate() {
            @Override
            public boolean test(Object obj) throws Exception {
                return filterClass.isAssignableFrom(obj.getClass());
            }
        });
    }

    private Observable getObservableProxyInternal(Object tag) {
        Observable res = null;
        if (getProxyType(tag) == ObservableRefProxy) {
            res = directObsMap.get(tag);
        } else {
            if (proxyObsMap.containsKey(tag)) {
                res = proxyObsMap.get(tag).pub();
            }
        }
        if (res == null) {
            res = createObservableProxy(tag);
        }
        return res;
    }


    private Observable createObservableProxy(Object tag) {
        Subject res;
        boolean isSafe = false;
        ProxyType proxyType = getProxyType(tag);
        if (proxyType instanceof RxJava2ObsProxyType) {
            switch ((RxJava2ObsProxyType) proxyType) {
                case BehaviorObsSafeProxy:
                    isSafe = true;
                case BehaviorSubjectProxy:
                    res = BehaviorSubject.create();
                    break;
                case PublishObsSafeProxy:
                    isSafe = true;
                case PublishSubjectProxy:
                    res = PublishSubject.create();
                    break;
                case ReplayObsSafeProxy:
                    isSafe = true;
                case ReplaySubjectProxy:
                    res = ReplaySubject.create();
                    break;
                case ObservableRefProxy:
                    throw new IllegalStateException("Cannot create ObservableRefProxy, " +
                            "it must be added before.");
                    //should not happen;
                default:
                    throw new IllegalStateException("Unknown ProxyType");
            }
        } else {
            //should not happen;
            throw new IllegalStateException("Unknown ProxyType");
        }

        if (isProxyThreadsafe(tag)) {
            res = res.toSerialized();
        }
        proxyObsMap.put(tag, new SubjProxy(res, isSafe ? WRAP : PASS));
        return res;
    }


    @Override
    public final void emit(Object tag, Object event) {
        if (!canTriggerEmit(tag)) {
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Emitting events on Tag(%s) not allowed.", tag));
        }
        ProxyType proxyType = getProxyType(tag);
        if (proxyType == ObservableRefProxy) {
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Emitting event not possible. Tag(%s) represents immutable stream.", tag));
        }
        Observable proxy = getObservableProxyInternal(tag);
        ((Observer) proxy).onNext(event);
    }

    @Override
    public final void clearUpstream() {
        for (Map.Entry<Object, SubjProxy> entries : proxyObsMap.entrySet()) {
            SubjProxy proxy = entries.getValue();
            proxy.clear();
        }
        directObsMap.clear();
    }


    @Override
    public void resetProxy(Object tag) {
        ProxyType proxyType = getProxyType(tag);
        if (proxyType instanceof RxJava2ObsProxyType) {
            SubjProxy proxy = proxyObsMap.get(tag);
            if (proxy != null) {
                proxy.clear();
                proxy.complete();
                proxyObsMap.remove(tag);
            }
        } else {
            directObsMap.remove(tag);
        }
    }

    @Override
    public void removeUpstream(Object tag) {
        ProxyType proxyType = getProxyType(tag);
        if (proxyType instanceof RxJava2ObsProxyType) {
            SubjProxy proxy = proxyObsMap.get(tag);
            if (proxy != null) {
                proxy.clear();
                proxyObsMap.remove(tag);
            }
        } else {
            directObsMap.remove(tag);
        }
    }


}
