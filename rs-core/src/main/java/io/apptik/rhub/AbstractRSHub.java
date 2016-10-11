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
