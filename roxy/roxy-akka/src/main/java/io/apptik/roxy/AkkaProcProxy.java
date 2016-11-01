package io.apptik.roxy;


import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import akka.stream.ActorMaterializer;
import akka.stream.javadsl.AsPublisher;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import static io.apptik.roxy.Roxy.TePolicy.PASS;
import static io.apptik.roxy.Roxy.TePolicy.WRAP;

/**
 * Roxy implementation using Akka Stream API
 */
public class AkkaProcProxy extends RSProcProxy<Publisher> {

    private final ActorMaterializer mat;
    private final Processor proc;
    private final TePolicy tePolicy;

    private final Map<Publisher, Subscription> subscriptions = new ConcurrentHashMap<>();
    private final AtomicInteger cnt;

    public AkkaProcProxy(Processor proc, TePolicy tePolicy, ActorMaterializer mat) {
        super(proc, tePolicy);
        this.proc = proc;
        this.tePolicy = tePolicy;
        this.mat = mat;
        cnt = new AtomicInteger(
                mat.settings().initialInputBufferSize()
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Publisher hide(Processor processor) {
        return (Publisher) Source
                .fromPublisher(processor)
                .runWith(Sink.asPublisher(AsPublisher.WITH_FANOUT), mat);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> Publisher<T> filter(Processor processor, final Class<T> filterClass) {
        Source src = Source.fromPublisher(processor)
                .filter(o -> filterClass.isAssignableFrom(o.getClass()));
        return (Publisher<T>) src.runWith(Sink.asPublisher(AsPublisher.WITH_FANOUT), mat);
    }

    @Override
    public void emit(Object event) {
        super.emit(event);
        cnt.decrementAndGet();
    }

    @Override
    public Removable addUpstream(Publisher publisher) {
        publisher.subscribe(new Subscriber() {

            Subscription s;

            @Override
            public void onSubscribe(final Subscription subscription) {
                s = subscription;
                subscriptions.put(publisher, s);
                s.request(cnt.get());
            }

            @Override
            public void onNext(Object o) {
                proc.onNext(o);
                cnt.decrementAndGet();
                if (cnt.compareAndSet(0, mat.settings().maxInputBufferSize())) {
                    try {
                        //todo super hack:
                        // Akka internal subscriber request chunks of data
                        // it might happen that we call onNext where internal subscriber did not
                        // request the next chunk. this is why we wait a little and try to use the
                        // same ratio as internals
                        Thread.sleep(33);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    s.request(mat.settings().maxInputBufferSize());
                }
            }

            @Override
            public void onError(Throwable t) {
                if (tePolicy.equals(WRAP)) {
                    proc.onNext(new Event.ErrorEvent(t));
                    cnt.decrementAndGet();
                    cnt.compareAndSet(0, mat.settings().maxInputBufferSize());
                } else if (tePolicy.equals(PASS)) {
                    proc.onError(t);
                }
            }

            @Override
            public void onComplete() {
                if (tePolicy.equals(WRAP)) {
                    proc.onNext(Event.COMPLETE);
                    cnt.decrementAndGet();
                    cnt.compareAndSet(0, mat.settings().maxInputBufferSize());
                } else if (tePolicy.equals(PASS)) {
                    proc.onComplete();
                }
            }
        });
        return () -> removeUpstream(publisher);
    }
}
