package io.apptik.rhub;


import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.functions.Predicate;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.processors.ReplayProcessor;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;

import static io.apptik.rhub.RSHub.CoreProxyType.PublisherRefProxy;
import static io.apptik.rhub.RxJava2Hub.RxJava2ObsProxyType.ObservableRefProxy;


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

    private final Map<Object, Proxy> proxyPubMap = new ConcurrentHashMap<>();
    private final Map<Object, Publisher> directPubMap = new ConcurrentHashMap<>();
    private final Map<Object, SubjProxy> proxyObsMap = new ConcurrentHashMap<>();
    private final Map<Object, Observable> directObsMap = new ConcurrentHashMap<>();

    @Override
    public final Removable addObsUpstream(final Object tag, final Observable observable) {
        checkIfObservableProxy(tag);
        if (getProxyType(tag) == ObservableRefProxy) {
            directObsMap.put(tag, observable);
        } else {
            getObservableProxyInternal(tag);
            proxyObsMap.get(tag).addObs(tag, observable);
        }
        return new Removable() {
            @Override
            public void remove() {
                AbstractRxJava2Hub.this.removeObsUpstream(tag, observable);
            }
        };
    }

    @Override
    public final Removable addUpstream(final Object tag, final Publisher publisher) {
        checkIfPublisherProxy(tag);
        if (getProxyType(tag) == PublisherRefProxy) {
            directPubMap.put(tag, publisher);
        } else {
            getPublisherProxyInternal(tag);
            proxyPubMap.get(tag).addPub(tag, publisher);
        }
        return new Removable() {
            @Override
            public void remove() {
                AbstractRxJava2Hub.this.removeUpstream(tag, publisher);
            }
        };
    }

    private void checkIfPublisherProxy(Object tag) {
        ProxyType proxyType = getProxyType(tag);
        if (!(proxyType instanceof RxJava2PubProxyType || proxyType instanceof CoreProxyType)) {
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Tag(%s) does not support Publisher proxy type(%s) !", tag, proxyType));
        }
    }

    private void checkIfObservableProxy(Object tag) {
        ProxyType proxyType = getProxyType(tag);
        if (!(proxyType instanceof RxJava2ObsProxyType)) {
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Tag(%s) does not support Observable proxy type(%s) !",
                    tag, proxyType));
        }
    }

    @Override
    public final void removeObsUpstream(Object tag, Observable observable) {
        checkIfObservableProxy(tag);
        if (getProxyType(tag) == ObservableRefProxy) {
            directObsMap.remove(tag);
        } else {
            SubjProxy proxy = proxyObsMap.get(tag);
            if (proxy != null) {
                proxy.removeObs(tag, observable);
            }
        }
    }

    @Override
    public final void removeUpstream(Object tag, Publisher publisher) {
        checkIfPublisherProxy(tag);
        if (getProxyType(tag) == PublisherRefProxy) {
            directPubMap.remove(tag);
        } else {
            Proxy proxy = proxyPubMap.get(tag);
            if (proxy != null) {
                proxy.removePub(tag, publisher);
            }
        }
    }

    @Override
    public final Observable getObservable(Object tag) {
        checkIfObservableProxy(tag);
        Observable res = getObservableProxyInternal(tag);
        if (getProxyType(tag) == ObservableRefProxy) {
            return res;
        } else {
            //make sure we expose it as Observable hide proxy's identity
            return res.hide();
        }
    }

    @Override
    public final Publisher getPub(Object tag) {
        checkIfPublisherProxy(tag);
        Publisher res = getPublisherProxyInternal(tag);
        if (getProxyType(tag) == PublisherRefProxy) {
            return res;
        } else {
            //make sure we expose it as Publisher hide proxy's identity
            return ((Flowable<Object>) res).hide();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> Observable<T> getObservable(Object tag, final Class<T> filterClass) {
        checkIfObservableProxy(tag);
        return getObservableProxyInternal(tag).filter(new Predicate() {
            @Override
            public boolean test(Object obj) throws Exception {
                return filterClass.isAssignableFrom(obj.getClass());
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> Publisher<T> getPub(Object tag, final Class<T> filterClass) {
        checkIfPublisherProxy(tag);
        Publisher res = getPublisherProxyInternal(tag);
        Predicate predicate = new Predicate() {
            @Override
            public boolean test(Object o) {
                return filterClass.isAssignableFrom(o.getClass());
            }
        };
        if (Flowable.class.isAssignableFrom(res.getClass())) {
            return ((Flowable) res).filter(predicate);
        } else {
            return Flowable.fromPublisher(res).filter(predicate);
        }
    }

    private Observable getObservableProxyInternal(Object tag) {
        checkIfObservableProxy(tag);
        Observable res = null;
        if (getProxyType(tag) == ObservableRefProxy) {
            res = directObsMap.get(tag);
        } else {
            if (proxyObsMap.containsKey(tag)) {
                res = proxyObsMap.get(tag).proc;
            }
        }
        if (res == null) {
            res = createObservableProxy(tag);
        }
        return res;
    }

    private Publisher getPublisherProxyInternal(Object tag) {
        checkIfPublisherProxy(tag);
        Publisher res = null;
        if (getProxyType(tag) == PublisherRefProxy) {
            res = directPubMap.get(tag);
        } else {
            if (proxyPubMap.containsKey(tag)) {
                res = proxyPubMap.get(tag).proc;
            }
        }
        if (res == null) {
            res = createPublisherProxy(tag);
        }
        return res;
    }

    private Observable createObservableProxy(Object tag) {
        checkIfObservableProxy(tag);
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
        proxyObsMap.put(tag, new SubjProxy(res, isSafe));
        return res;
    }


    private Flowable createPublisherProxy(Object tag) {
        checkIfPublisherProxy(tag);
        FlowableProcessor res;
        boolean isSafe = false;
        ProxyType proxyType = getProxyType(tag);
        if (proxyType instanceof RxJava2PubProxyType) {
            switch ((RxJava2PubProxyType) proxyType) {
                case BehaviorSafeProxy:
                    isSafe = true;
                case BehaviorProcessorProxy:
                    res = BehaviorProcessor.create();
                    break;
                case PublishSafeProxy:
                    isSafe = true;
                case PublishProcessorProxy:
                    res = PublishProcessor.create();
                    break;
                case ReplaySafeProxy:
                    isSafe = true;
                case ReplayProcessorProxy:
                    res = ReplayProcessor.create();
                    break;
                //should not happen;
                default:
                    throw new IllegalStateException("Unknown ProxyType");
            }
        } else if (proxyType instanceof CoreProxyType) {
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
            res = res.toSerialized();
        }
        proxyPubMap.put(tag, new Proxy(res, isSafe));
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
        if (proxyType instanceof RxJava2PubProxyType) {
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
    public final void clearObsUpstream() {
        for (Map.Entry<Object, SubjProxy> entries : proxyObsMap.entrySet()) {
            SubjProxy proxy = entries.getValue();
            proxy.clear();
        }
        directObsMap.clear();
    }

    @Override
    public final void clearUpstream() {
        for (Map.Entry<Object, Proxy> entries : proxyPubMap.entrySet()) {
            ProxyType proxyType = getProxyType(entries.getKey());
            Proxy proxy = entries.getValue();
            proxy.clear();
        }
        directPubMap.clear();
    }

    @Override
    public void resetProxy(Object tag) {
        checkIfPublisherProxy(tag);
        ProxyType proxyType = getProxyType(tag);
        if (proxyType instanceof RxJava2PubProxyType) {
            Proxy proxy = proxyPubMap.get(tag);
            if (proxy != null) {
                proxy.clear();
                proxy.proc.onComplete();
                proxyPubMap.remove(tag);
            }
        } else {
            directPubMap.remove(tag);
        }
    }

    @Override
    public void resetObsProxy(Object tag) {
        checkIfObservableProxy(tag);
        ProxyType proxyType = getProxyType(tag);
        if (proxyType instanceof RxJava2ObsProxyType) {
            SubjProxy proxy = proxyObsMap.get(tag);
            if (proxy != null) {
                proxy.clear();
                proxy.proc.onComplete();
                proxyObsMap.remove(tag);
            }
        } else {
            directObsMap.remove(tag);
        }
    }

    @Override
    public void removeUpstream(Object tag) {
        checkIfPublisherProxy(tag);
        ProxyType proxyType = getProxyType(tag);
        if (proxyType instanceof RxJava2PubProxyType) {
            Proxy proxy = proxyPubMap.get(tag);
            if (proxy != null) {
                proxy.clear();
                proxyPubMap.remove(tag);
            }
        } else {
            directPubMap.remove(tag);
        }
    }

    @Override
    public void removeObsUpstream(Object tag) {
        checkIfObservableProxy(tag);
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
