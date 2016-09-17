package io.apptik.rxhub;


import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.processors.ReplayProcessor;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;

import static io.apptik.rxhub.RxHub.CoreProxyType.PublisherRefProxy;
import static io.apptik.rxhub.RxJava2Hub.RxJava2ProxyType.BehaviorProcessorProxy;
import static io.apptik.rxhub.RxJava2Hub.RxJava2ProxyType.BehaviorSubjectProxy;
import static io.apptik.rxhub.RxJava2Hub.RxJava2ProxyType.ObservableRefProxy;
import static io.apptik.rxhub.RxJava2Hub.RxJava2ProxyType.PublishProcessorProxy;
import static io.apptik.rxhub.RxJava2Hub.RxJava2ProxyType.PublishSubjectProxy;
import static io.apptik.rxhub.RxJava2Hub.RxJava2ProxyType.ReplayProcessorProxy;
import static io.apptik.rxhub.RxJava2Hub.RxJava2ProxyType.ReplaySubjectProxy;


/**
 * Base implementation of {@link RxJava2Hub}
 * Essentially this is a collection of {@link Observable} ND {@link Publisher} proxies which can
 * also
 * subscribe to other Observables and pass events to their Subscribers
 * <p/>
 * Proxies can be either {@link Subject} or {@link Processor}. Proxies are identified by their Tags.
 * Proxies subscribes to Observables however each subscription created is
 * per {@link Source}. A Source is identified by Observable and a Tag.
 * For example when Observable A is added with Tag T1 and Tag T2. Two proxies are created receiving
 * the same events. Each of those proxies can be used and unsubscribed from Observable A
 * independently.
 * <p/>
 * Observers subscribe to a Proxy. Observers does not need to know about the source of the Events
 * i.e the Observers that the Proxies is subscribed to.
 * <p/>
 * To fetch the Proxy to subscribe to {@link AbstractRxJava2Hub#getObservable(Object)} must be
 * called.
 * <p/>
 * Non-Rx code can also call {@link AbstractRxJava2Hub#emit(Object, Object)} to manually emit Events
 * through specific Proxy.
 */
public abstract class AbstractRxJava2Hub implements RxJava2Hub {

    private final Map<Object, Flowable> publisherProxyMap = new ConcurrentHashMap<>();
    private final Map<Object, Observable> observableProxyMap = new ConcurrentHashMap<>();
    private final Map<Source, Disposable>
            publisherSubscriptionMap = new ConcurrentHashMap<>();
    private final Map<ObservableSource, Disposable>
            observableSubscriptionMap = new ConcurrentHashMap<>();
    private final CompositeDisposable publisherDisposables = new CompositeDisposable();
    private final CompositeDisposable observableDisposables = new CompositeDisposable();


    @Override
    public final void addObservable(final Object tag, final Observable observable) {
        checkIfObservableProxy(tag);
        if (getProxyType(tag) == ObservableRefProxy) {
            observableProxyMap.put(tag, observable);
        } else {
            Observable proxy = getObservableProxyInternal(tag);
            ConnectableObservable cObs = observable.publish();

            //check if we are still cool
            if (Subject.class.isAssignableFrom(proxy.getClass())) {
                cObs.subscribe((Observer) proxy);
                cObs.connect(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        observableDisposables.add(disposable);
                        observableSubscriptionMap.put(
                                new ObservableSource(observable, tag), disposable);
                    }
                });
            } else {
                //should not happen
                throw new IllegalStateException(String.format(Locale.ENGLISH,
                        "Proxy(%s) type(%s) is not supported! Do we have an alien injection?",
                        tag, observable.getClass()));
            }
        }
    }

    @Override
    public final void addPub(final Object tag,final Flowable publisher) {
        checkIfPublisherProxy(tag);
        if (getProxyType(tag) == PublisherRefProxy) {
            publisherProxyMap.put(tag, publisher);
        } else {
            Publisher proxy = getPublisherProxyInternal(tag);
            ConnectableFlowable cFlowable = publisher.publish();
            //check if we are still cool
            if (Processor.class.isAssignableFrom(proxy.getClass())) {
                cFlowable.subscribe((Subscriber) proxy);
                cFlowable.connect(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        publisherDisposables.add(disposable);
                        publisherSubscriptionMap.put(
                                new Source(publisher, tag), disposable);
                    }
                });
            } else {
                //should not happen
                throw new IllegalStateException(String.format(Locale.ENGLISH,
                        "Proxy(%s) type(%s) is not supported! Do we have an alien injection?",
                        tag, publisher.getClass()));
            }
        }
    }

    private void checkIfPublisherProxy(Object tag) {
        ProxyType proxyType = getProxyType(tag);
        if (ObservableRefProxy.equals(proxyType)
                || BehaviorSubjectProxy.equals(proxyType)
                || PublishSubjectProxy.equals(proxyType)
                || ReplaySubjectProxy.equals(proxyType)
                ) {
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Tag(%s) does not support Publisher proxy type(%s) !",
                    tag, proxyType));
        }
    }

    private void checkIfObservableProxy(Object tag) {
        ProxyType proxyType = getProxyType(tag);
        if (PublisherRefProxy.equals(proxyType)
                || BehaviorProcessorProxy.equals(proxyType)
                || PublishProcessorProxy.equals(proxyType)
                || ReplayProcessorProxy.equals(proxyType)
                ) {
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Tag(%s) does not support Observable proxy type(%s) !",
                    tag, proxyType));
        }
    }

    @Override
    public final void removeObservable(Object tag, Observable observable) {
        checkIfObservableProxy(tag);
        if (getProxyType(tag) == ObservableRefProxy) {
            observableProxyMap.remove(tag);
        } else {
            ObservableSource s = new ObservableSource(observable, tag);
            if (observableSubscriptionMap.containsKey(s)) {
                observableDisposables.remove(observableSubscriptionMap.get(s));
                observableSubscriptionMap.remove(s);
            }
        }
    }

    @Override
    public final void removePub(Object tag, Flowable publisher) {
        checkIfPublisherProxy(tag);
        if (getProxyType(tag) == PublisherRefProxy) {
            publisherProxyMap.remove(tag);
        } else {
            Source s = new Source(publisher, tag);
            if (publisherSubscriptionMap.containsKey(s)) {
                publisherDisposables.remove(publisherSubscriptionMap.get(s));
                publisherSubscriptionMap.remove(s);
            }
        }
    }

    @Override
    public final Observable getObservable(Object tag) {
        //make sure we expose it as Observable hide proxy's identity
        return getObservableProxyInternal(tag).hide();
    }

    @Override
    public final Flowable getPub(Object tag) {
        //make sure we expose it as Publisher hide proxy's identity
        return getPublisherProxyInternal(tag).hide();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> Observable<T> getObservable(Object tag, final Class<T> filterClass) {
        return getObservable(tag).filter(new Predicate() {
            @Override
            public boolean test(Object obj) throws Exception {
                return filterClass.isAssignableFrom(obj.getClass());
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> Flowable<T> getPub(Object tag, final Class<T> filterClass) {
        return getPub(tag).filter(new Predicate() {
            @Override
            public boolean test(Object obj) throws Exception {
                return filterClass.isAssignableFrom(obj.getClass());
            }
        });
    }


    private Observable getObservableProxyInternal(Object tag) {
        Observable res = observableProxyMap.get(tag);
        if (res == null) {
            res = createObservableProxy(tag);
        }
        return res;
    }

    private Flowable getPublisherProxyInternal(Object tag) {
        Flowable res = publisherProxyMap.get(tag);
        if (res == null) {
            res = createPublisherProxy(tag);
        }
        return res;
    }

    private Observable createObservableProxy(Object tag) {
        Subject res;
        ProxyType proxyType = getProxyType(tag);
        if (proxyType instanceof RxJava2ProxyType) {
            switch ((RxJava2ProxyType) proxyType) {
                case BehaviorSubjectProxy:
                    res = BehaviorSubject.create();
                    break;
                case PublishSubjectProxy:
                    res = PublishSubject.create();
                    break;
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
            switch ((RxJava2ProxyType) proxyType) {
                case BehaviorSubjectProxy:
                case PublishSubjectProxy:
                case ReplaySubjectProxy:
                    res = res.toSerialized();
                    break;
            }
        }
        observableProxyMap.put(tag, res);
        return res;
    }


    private Flowable createPublisherProxy(Object tag) {
        FlowableProcessor res;
        ProxyType proxyType = getProxyType(tag);
        if (proxyType instanceof RxJava2ProxyType) {
            switch ((RxJava2ProxyType) proxyType) {
                case BehaviorProcessorProxy:
                    res = BehaviorProcessor.create();
                    break;
                case PublishProcessorProxy:
                    res = PublishProcessor.create();
                    break;
                case ReplayProcessorProxy:
                    res = ReplayProcessor.create();
                    break;
                //should not happen;
                default:
                    throw new IllegalStateException("Unknown ProxyType");
            }
        } else if (proxyType instanceof RxJava2ProxyType) {
            switch ((CoreProxyType) proxyType) {
                case PublisherRefProxy:
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
            switch ((RxJava2ProxyType) proxyType) {
                case BehaviorProcessorProxy:
                case PublishProcessorProxy:
                case ReplayProcessorProxy:
                    res = res.toSerialized();
                    break;
            }
        }
        publisherProxyMap.put(tag, res);
        return res;
    }


    @Override
    public final void emit(Object tag, Object event) {
        if (!canTriggerEmit(tag)) {
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Emitting events on Tag(%s) not allowed.", tag));
        }
        ProxyType proxyType = getProxyType(tag);
        if (proxyType == ObservableRefProxy
                || proxyType == PublisherRefProxy) {
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Emitting event not possible. Tag(%s) represents immutable stream.", tag));
        }
        Object proxy;
        if (proxyType == BehaviorProcessorProxy
                || proxyType == PublishProcessorProxy
                || proxyType == ReplayProcessorProxy) {
            proxy = getPublisherProxyInternal(tag);
        } else {
            proxy = getObservableProxyInternal(tag);
        }
        if (Subject.class.isAssignableFrom(proxy.getClass())) {
            ((Observer) proxy).onNext(event);
        } else if (Processor.class.isAssignableFrom(proxy.getClass())) {
            ((Processor) proxy).onNext(event);
        } else {
            //should not happen
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Proxy(%s) type(%s) is not supported! Do we have an alien injection?",
                    tag, proxy.getClass()));
        }
    }

    @Override
    public final void clearObservables() {
        observableDisposables.clear();
        observableSubscriptionMap.clear();
    }

    @Override
    public final void clearPublishers() {
        publisherDisposables.clear();
        publisherSubscriptionMap.clear();
    }

}
