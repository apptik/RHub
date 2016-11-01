package io.apptik.roxy;


import org.reactivestreams.Publisher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import akka.Done;
import akka.NotUsed;
import akka.japi.Pair;
import akka.japi.function.Predicate;
import akka.stream.ActorMaterializer;
import akka.stream.KillSwitch;
import akka.stream.KillSwitches;
import akka.stream.UniqueKillSwitch;
import akka.stream.javadsl.AsPublisher;
import akka.stream.javadsl.BroadcastHub;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.MergeHub;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

/**
 * Roxy implementation using Akka Streams
 */
public class AkkaHubProxy implements Roxy<Source<Object, NotUsed>> {

    private final ActorMaterializer mat;
    private final Source<Object, NotUsed> source;

    private final Flow<Object, Object, UniqueKillSwitch> busFlow;
    private final Map<Source, KillSwitch> subscriptions = new ConcurrentHashMap<>();

    public AkkaHubProxy(ActorMaterializer mat) {
        this.mat = mat;
        //  Obtain a Sink and Source which will publish and receive from the "bus" respectively.
        Pair<Sink<Object, NotUsed>, Source<Object, NotUsed>> sinkAndSource =
                MergeHub.of(Object.class, 16)
                        .toMat(BroadcastHub.of(Object.class, 256), Keep.both())
                        .run(mat);

        Sink<Object, NotUsed> sink = sinkAndSource.first();
        source = sinkAndSource.second().takeWhile((Predicate<Object>) o -> o != Done.getInstance());
        //source.runWith(Sink.ignore(), mat);
        busFlow = Flow.fromSinkAndSource(sink, source)
                .joinMat(KillSwitches.singleBidi(), Keep.right());
    }

    @Override
    public Removable addUpstream(Source<Object, NotUsed> publisher) {
        UniqueKillSwitch killSwitch =
                publisher.viaMat(busFlow, Keep.right())
                        .to(Sink.ignore())
                        .run(mat);
        subscriptions.put(publisher, killSwitch);
        return () -> AkkaHubProxy.this.removeUpstream(publisher);
    }

    @Override
    public void removeUpstream(Source publisher) {
        synchronized (subscriptions) {
            KillSwitch s = subscriptions.get(publisher);
            if (s != null) {
                s.shutdown();
                subscriptions.remove(publisher);
            }
        }
    }

    @Override
    public Source<Object, NotUsed> pub() {
        return source;
    }

    @Override
    public <T> Source pub(Class<T> filterClass) {
        return source.filter(o -> filterClass.isAssignableFrom(o.getClass()));
    }

    @Override
    public void emit(Object event) {
        Source.single(event).viaMat(busFlow, Keep.right())
                .to(Sink.ignore())
                .run(mat);
    }

    @Override
    public void complete() {
        emit(Done.getInstance());
    }

    @Override
    public void clear() {
        synchronized (subscriptions) {
            for (KillSwitch s : subscriptions.values()) {
                s.shutdown();
            }
            subscriptions.clear();
        }
    }

    @Override
    public TePolicy tePolicy() {
        return TePolicy.SKIP;
    }

    public Publisher<Object> asPublisher() {
        return source.runWith(Sink.asPublisher(AsPublisher.WITH_FANOUT), mat);
    }
}
