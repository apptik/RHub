package io.apptik.rxhub;


import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.publisher.TopicProcessor;
import reactor.core.publisher.WorkQueueProcessor;

import static io.apptik.rxhub.RxHub.CoreProxyType.PublisherRefProxy;

public abstract class AbstractReactorHub implements ReactorHub {

    private final Map<Object, Proxy> proxyPubMap = new ConcurrentHashMap<>();
    private final Map<Object, Publisher> directPubMap = new ConcurrentHashMap<>();

    @Override
    public void removePub(Object tag, Publisher publisher) {
        ProxyType proxyType = getProxyType(tag);
        if (proxyType instanceof ReactorProxyType) {
            Proxy proxy = proxyPubMap.get(tag);
            if (proxy != null) {
                proxy.removePub(tag, publisher);
            }
        } else if (proxyType instanceof CoreProxyType) {
            directPubMap.remove(tag);
        } else {
            //should not happen;
            throw new IllegalStateException("Unknown ProxyType");
        }
    }

    @Override
    public void clearPublishers() {
        for (Map.Entry<Object, Proxy> entries : proxyPubMap.entrySet()) {
            ProxyType proxyType = getProxyType(entries.getKey());
            Proxy proxy = entries.getValue();
            if (proxyType instanceof ReactorProxyType) {
                proxy.clear();
            }
        }
        directPubMap.clear();
    }

    @Override
    public void emit(Object tag, Object event) {
        if (!canTriggerEmit(tag)) {
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Emitting events on Tag(%s) not allowed.", tag));
        }
        ProxyType proxyType = getProxyType(tag);
        if (proxyType == PublisherRefProxy) {
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Emitting event not possible. Tag(%s) represents immutable stream.", tag));
        }
        Object proxy = getPublisherProxyInternal(tag);
        if (Processor.class.isAssignableFrom(proxy.getClass())) {
            ((Processor) proxy).onNext(event);
        } else {
            //should not happen
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Proxy(%s) type(%s) is not supported! Do we have an alien injection?",
                    tag, proxy.getClass()));
        }
    }

    @Override
    public void addPub(Object tag, Publisher publisher) {
        if (getProxyType(tag) == PublisherRefProxy) {
            directPubMap.put(tag, publisher);
        } else {
            getPublisherProxyInternal(tag);
            proxyPubMap.get(tag).addPub(tag, publisher);
        }
    }

    @Override
    public final Publisher getPub(Object tag) {
        //make sure we expose it as Publisher hide proxy's identity
        ProxyType proxyType = getProxyType(tag);
        Publisher res = getPublisherProxyInternal(tag);
        if (proxyType instanceof ReactorProxyType) {
            return ((Flux) res).hide();
        } else {
            return res;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> Publisher<T> getPub(Object tag, final Class<T> filterClass) {
        Publisher res = getPublisherProxyInternal(tag);
        ProxyType proxyType = getProxyType(tag);
        Predicate predicate = new Predicate() {
            @Override
            public boolean test(Object o) {
                return filterClass.isAssignableFrom(o.getClass());
            }
        };
        if (proxyType instanceof ReactorProxyType) {
            return ((Flux) res).filter(predicate);
        } else {
            return Flux.from(res).filter(predicate);
        }
    }

    private Publisher getPublisherProxyInternal(Object tag) {
        Publisher res = null;
        ProxyType proxyType = getProxyType(tag);
        if (proxyType instanceof ReactorProxyType) {
            if (proxyPubMap.containsKey(tag)) {
                res = proxyPubMap.get(tag).proc;
            }
        } else {
            res = directPubMap.get(tag);
        }
        if (res == null) {
            res = createPublisherProxy(tag);
        }
        return res;
    }

    private Flux createPublisherProxy(Object tag) {
        FluxProcessor res;
        boolean isSafe = false;
        ProxyType proxyType = getProxyType(tag);
        if (proxyType instanceof ReactorProxyType) {
            switch ((ReactorProxyType) proxyType) {
                case EmitterSafeProxy:
                    isSafe = true;
                case EmitterProcessorProxy:
                    res = EmitterProcessor.create().connect();
                    break;
                case TopicSafeProxy:
                    isSafe = true;
                case TopicProcessorProxy:
                    res = TopicProcessor.create();
                    break;
                case BehaviorSafeProxy:
                    isSafe = true;
                case BehaviorProcessorProxy:
                    res = ReplayProcessor.create(1);
                    break;
                case ReplaySafeProxy:
                    isSafe = true;
                case ReplayProcessorProxy:
                    res = ReplayProcessor.create();
                    break;
                case WorkQueueSafeProxy:
                    isSafe = true;
                case WorkQueueProcessorProxy:
                    res = WorkQueueProcessor.create();
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
            res = res.serialize();
        }
        proxyPubMap.put(tag, new Proxy(res, isSafe));
        return res;
    }

    @Override
    public void resetProxy(Object tag) {
        ProxyType proxyType = getProxyType(tag);
        if (proxyType instanceof ReactorProxyType) {
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
    public void removeAllPub(Object tag) {
        ProxyType proxyType = getProxyType(tag);
        if (proxyType instanceof ReactorProxyType) {
            Proxy proxy = proxyPubMap.get(tag);
            if (proxy != null) {
                proxy.clear();
                proxyPubMap.remove(tag);
            }
        } else {
            directPubMap.remove(tag);
        }
    }
}
