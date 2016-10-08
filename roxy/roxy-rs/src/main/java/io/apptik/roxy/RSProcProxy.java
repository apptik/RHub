package io.apptik.roxy;


import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.apptik.roxy.Roxy.TePolicy.PASS;
import static io.apptik.roxy.Roxy.TePolicy.WRAP;

public abstract class RSProcProxy<P extends Publisher> implements Roxy<P> {

    private final Processor proc;
    private final TePolicy tePolicy;

    private final Map<Publisher, Subscription> subscriptions = new ConcurrentHashMap<>();

    public RSProcProxy(Processor proc, TePolicy tePolicy) {
        this.proc = proc;
        this.tePolicy = tePolicy;
    }

    /**
     * Subscribes RSProcProxy to {@link Publisher}.
     * If there is no RSProcProxy with the specific tag a new one will be created
     *
     * @param publisher the Publisher to subscribe to
     */
    @Override
    public Removable addUpstream(final Publisher publisher) {
        publisher.subscribe(new Subscriber() {
            @Override
            public void onSubscribe(final Subscription s) {
                subscriptions.put(publisher, s);
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Object o) {
                proc.onNext(o);
            }

            @Override
            public void onError(Throwable t) {
                if (tePolicy.equals(WRAP)) {
                    proc.onNext(new Event.ErrorEvent(t));
                } else if (tePolicy.equals(PASS)) {
                    proc.onError(t);
                }
            }

            @Override
            public void onComplete() {
                if (tePolicy.equals(WRAP)) {
                    proc.onNext(Event.COMPLETE);
                } else if (tePolicy.equals(PASS)) {
                    proc.onComplete();
                }
            }
        });
        return new Removable() {
            @Override
            public void remove() {
                removeUpstream(publisher);
            }
        };
    }

    /**
     * Unsubscribe {@link Publisher} from a RSProcProxy
     *
     * @param publisher the Publisher to unsubscribe from
     */
    @Override
    public void removeUpstream(Publisher publisher) {
        synchronized (subscriptions) {
            Subscription s = subscriptions.get(publisher);
            if (s != null) {
                s.cancel();
                subscriptions.remove(publisher);
            }
        }
    }

    @Override
    public P pub() {
        //make sure we expose it as Publisher hide proxy's identity
        return hide(proc);
    }

    @Override
    public <T> P pub(Class<T> filterClass) {
        return filter(proc, filterClass);
    }

    @Override
    public void emit(Object event) {
        proc.onNext(event);
    }

    @Override
    public void complete() {
        proc.onComplete();
    }

    @Override
    public TePolicy tePolicy() {
        return tePolicy;
    }

    @Override
    public void clear() {
        synchronized (subscriptions) {
            for (Subscription s : subscriptions.values()) {
                s.cancel();
            }
            subscriptions.clear();
        }
    }


    /**
     * Hides processor identity
     * @param processor
     * @return
     */
    abstract P hide(Processor processor);

    abstract <T> P filter(Processor processor, Class<T> filterClass);
}
