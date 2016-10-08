package io.apptik.rhub;


import org.reactivestreams.Publisher;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.apptik.roxy.RSProcProxy;
import io.apptik.roxy.Removable;
import io.apptik.roxy.Roxy;


/**
 * Base implementation of {@link RSHub}
 * Essentially this is a collection of proxies which can
 * also
 * subscribe to other Observables and pass events to their Subscribers
 * <p/>
 * Proxies are identified by their Tags.
 * Proxies subscribes to Observables however each subscription created is
 * per Source. A Source is identified by Observable and a Tag.
 * For example when Observable A is added with Tag T1 and Tag T2. Two proxies are created receiving
 * the same events. Each of those proxies can be used and unsubscribed from Observable A
 * independently.
 * <p/>
 * Observers subscribe to a RSProcProxy. Observers does not need to know about the source of the Events
 * i.e the Observers that the Proxies is subscribed to.
 * <p/>
 * To fetch the RSProcProxy to subscribe to {@link AbstractRSHub#getPub(Object)} must be
 * called.
 * <p/>
 * Non-Rx code can also call {@link AbstractRSHub#emit(Object, Object)} to manually emit
 * Events
 * through specific RSProcProxy.
 */
public abstract class AbstractRSHub<P extends Publisher> implements RSHub<P> {


    private final Map<Object, RSProcProxy<P>> proxyMap = new ConcurrentHashMap<>();

    @Override
    public final Removable addUpstream(final Object tag, final Publisher publisher) {
        getProxyInternal(tag).addUpstream(publisher);
        return new Removable() {
            @Override
            public void remove() {
                AbstractRSHub.this.removeUpstream(tag, publisher);
            }
        };
    }

    @Override
    public final void removeUpstream(Object tag, Publisher publisher) {
        RSProcProxy proxy = proxyMap.get(tag);
        if (proxy != null) {
            proxy.removeUpstream(publisher);
        }
    }

    @Override
    public final P getPub(Object tag) {
        return getProxyInternal(tag).pub();
    }

    @Override
    public <T> P getPub(Object tag, Class<T> filterClass) {
        return getProxyInternal(tag).pub(filterClass);
    }

    private RSProcProxy<P> getProxyInternal(Object tag) {
        RSProcProxy<P> proxy = proxyMap.get(tag);
        if (proxy == null) {
            proxy = createProxy(tag);
        }
        return proxy;
    }

    private RSProcProxy<P> createProxy(Object tag) {
        ProxyType<RSProcProxy<P>> proxyType = getProxyType(tag);
        RSProcProxy<P> roxy = proxyType.getRoxy();
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
        for (Map.Entry<Object, RSProcProxy<P>> entries : proxyMap.entrySet()) {
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
