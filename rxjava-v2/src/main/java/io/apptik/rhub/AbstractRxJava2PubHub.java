package io.apptik.rhub;


import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.apptik.rhub.RxJava2ObsHub.RxJava2PubProxyType;
import io.apptik.roxy.RSProxy;
import io.apptik.roxy.Removable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.processors.ReplayProcessor;
import io.reactivex.subjects.Subject;

import static io.apptik.rhub.RSHub.CoreProxyType.PublisherRefProxy;
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
 * per Source. A Source is identified by Observable and a Tag.
 * For example when Observable A is added with Tag T1 and Tag T2. Two proxies are created receiving
 * the same events. Each of those proxies can be used and unsubscribed from Observable A
 * independently.
 * <p/>
 * Observers subscribe to a RSProxy. Observers does not need to know about the source of the Events
 * i.e the Observers that the Proxies is subscribed to.
 * <p/>
 * To fetch the RSProxy to subscribe to {@link AbstractRxJava2PubHub#getPub(Object)} must be
 * called.
 * <p/>
 * Non-Rx code can also call {@link AbstractRxJava2PubHub#emit(Object, Object)} to manually emit
 * Events
 * through specific RSProxy.
 */
public abstract class AbstractRxJava2PubHub implements RSHub {

    private final Map<Object, RSProxy> proxyPubMap = new ConcurrentHashMap<>();
    private final Map<Object, Publisher> directPubMap = new ConcurrentHashMap<>();


    @Override
    public final Removable addUpstream(final Object tag, final Publisher publisher) {
        if (getProxyType(tag) == PublisherRefProxy) {
            directPubMap.put(tag, publisher);
        } else {
            getPublisherProxyInternal(tag);
            proxyPubMap.get(tag).addUpstream(publisher);
        }
        return new Removable() {
            @Override
            public void remove() {
                AbstractRxJava2PubHub.this.removeUpstream(tag, publisher);
            }
        };
    }

    @Override
    public final void removeUpstream(Object tag, Publisher publisher) {
        if (getProxyType(tag) == PublisherRefProxy) {
            directPubMap.remove(tag);
        } else {
            RSProxy proxy = proxyPubMap.get(tag);
            if (proxy != null) {
                proxy.removeUpstream(publisher);
            }
        }
    }

    @Override
    public final Publisher getPub(Object tag) {
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
    public final <T> Publisher<T> getPub(Object tag, final Class<T> filterClass) {
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

    private Publisher getPublisherProxyInternal(Object tag) {
        Publisher res = null;
        if (getProxyType(tag) == PublisherRefProxy) {
            res = directPubMap.get(tag);
        } else {
            if (proxyPubMap.containsKey(tag)) {
                res = proxyPubMap.get(tag).pub();
            }
        }
        if (res == null) {
            res = createPublisherProxy(tag);
        }
        return res;
    }


    private Flowable createPublisherProxy(Object tag) {
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
        proxyPubMap.put(tag, new RSProxy(res, isSafe ? WRAP : PASS));
        return res;
    }


    @Override
    public final void emit(Object tag, Object event) {
        if (!canTriggerEmit(tag)) {
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Emitting events on Tag(%s) not allowed.", tag));
        }
        ProxyType proxyType = getProxyType(tag);
        if (proxyType == PublisherRefProxy) {
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Emitting event not possible. Tag(%s) represents immutable stream.", tag));
        }
        Publisher proxy = getPublisherProxyInternal(tag);
        ((Processor) proxy).onNext(event);

    }

    @Override
    public final void clearUpstream() {
        for (Map.Entry<Object, RSProxy> entries : proxyPubMap.entrySet()) {
            ProxyType proxyType = getProxyType(entries.getKey());
            RSProxy proxy = entries.getValue();
            proxy.clear();
        }
        directPubMap.clear();
    }

    @Override
    public void resetProxy(Object tag) {
        ProxyType proxyType = getProxyType(tag);
        if (proxyType instanceof RxJava2PubProxyType) {
            RSProxy proxy = proxyPubMap.get(tag);
            if (proxy != null) {
                proxy.clear();
                proxy.complete();
                proxyPubMap.remove(tag);
            }
        } else {
            directPubMap.remove(tag);
        }
    }

    @Override
    public void removeUpstream(Object tag) {
        ProxyType proxyType = getProxyType(tag);
        if (proxyType instanceof RxJava2PubProxyType) {
            RSProxy proxy = proxyPubMap.get(tag);
            if (proxy != null) {
                proxy.clear();
                proxyPubMap.remove(tag);
            }
        } else {
            directPubMap.remove(tag);
        }
    }
}
