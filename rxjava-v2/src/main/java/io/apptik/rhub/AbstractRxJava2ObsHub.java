package io.apptik.rhub;


import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.apptik.roxy.Removable;
import io.apptik.roxy.Roxy;
import io.apptik.roxy.RxJava2SubjProxy;
import io.reactivex.Observable;
import io.reactivex.subjects.Subject;


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
 * Observers subscribe to a RSProcProxy. Observers does not need to know about the source of the
 * Events
 * i.e the Observers that the Proxies is subscribed to.
 * <p/>
 * To fetch the RSProcProxy to subscribe to {@link AbstractRxJava2ObsHub#getPub(Object)} must be
 * called.
 * <p/>
 * Non-Rx code can also call {@link AbstractRxJava2ObsHub#emit(Object, Object)} to manually emit
 * Events
 * through specific RSProcProxy.
 */
public abstract class AbstractRxJava2ObsHub implements RxJava2ObsHub {

    private final Map<Object, RxJava2SubjProxy> proxyMap = new ConcurrentHashMap<>();

    @Override
    public final Removable addUpstream(final Object tag, final Observable observable) {
        getProxyInternal(tag).addUpstream(observable);
        return new Removable() {
            @Override
            public void remove() {
                AbstractRxJava2ObsHub.this.removeUpstream(tag, observable);
            }
        };
    }

    @Override
    public final void removeUpstream(Object tag, Observable observable) {
        Roxy<Observable> proxy = proxyMap.get(tag);
        if (proxy != null) {
            proxy.removeUpstream(observable);
        }
    }

    @Override
    public final Observable getPub(Object tag) {
        return getProxyInternal(tag).pub();

    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> Observable<T> getPub(Object tag, Class<T> filterClass) {
        return getProxyInternal(tag).pub(filterClass);
    }

    private Roxy<Observable> getProxyInternal(Object tag) {
        Roxy<Observable> proxy = proxyMap.get(tag);
        if (proxy == null) {
            proxy = createProxy(tag);
        }
        return proxy;
    }

    private RxJava2SubjProxy createProxy(Object tag) {
        RxJava2SubjProxy roxy = getProxyType(tag).getRoxy();
        proxyMap.put(tag, roxy);
        return roxy;
    }

    @Override
    public final void emit(Object tag, Object event) {
        if (!canTriggerEmit(tag)) {
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Emitting events on Tag(%s) not allowed.", tag));
        }
        getProxyInternal(tag).emit(event);
    }


    @Override
    public final void clearUpstream() {
        for (Map.Entry<Object, RxJava2SubjProxy> entries : proxyMap.entrySet()) {
            Roxy proxy = entries.getValue();
            proxy.clear();
        }
    }

    @Override
    public void resetProxy(Object tag) {
        Roxy proxy = proxyMap.get(tag);
        if (proxy != null) {
            proxy.clear();
            proxy.complete();
            proxyMap.remove(tag);
        }
    }

    @Override
    public void removeUpstream(Object tag) {
        Roxy proxy = proxyMap.get(tag);
        if (proxy != null) {
            proxy.clear();
            proxyMap.remove(tag);
        }
    }
}
