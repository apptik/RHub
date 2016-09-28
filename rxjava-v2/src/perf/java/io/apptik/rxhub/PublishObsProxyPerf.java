package io.apptik.rxhub;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class PublishObsProxyPerf {

    //@Benchmark
    public void emit(States.ObjProxyParamsEmit p, Blackhole bh) throws InterruptedException {
        int s = p.subscribers;
        CountDownLatch latch = new CountDownLatch(s);

        for (int i = 0; i < s; i++) {
            p.obs.subscribe(new LatchedObserver<Integer>(bh, latch));
        }

        int c = p.count;
        for (int i = 0; i < c; i++) {
            p.hub.emit(p.tag, 777);
        }
        p.hub.resetObsProxy(p.tag);
        latch.await();
        bh.consume(latch);
    }

    //@Benchmark
    public void observe(States.ObjProxyParamsUpstream p, Blackhole bh) throws
            InterruptedException {
        int s = p.subscribers;
        CountDownLatch latch = new CountDownLatch(s);
        for (int i = 0; i < s; i++) {
            p.obs.subscribe(new LatchedObserver<Integer>(bh, latch));
        }

        p.upstream.connect();
        latch.await();
        bh.consume(latch);
    }
}
