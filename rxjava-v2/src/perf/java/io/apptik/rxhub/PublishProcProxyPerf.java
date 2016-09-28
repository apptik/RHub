package io.apptik.rxhub;

import org.openjdk.jmh.annotations.Benchmark;
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
public class PublishProcProxyPerf {

    @Benchmark
    public void emit(States.ProcProxyParamsEmit p, Blackhole bh) throws InterruptedException {
        int s = p.subscribers;
        CountDownLatch latch = new CountDownLatch(s);

        for (int i = 0; i < s; i++) {
            p.obs.subscribe(new LatchedSubscriber<Integer>(bh, latch));
        }

        int c = p.count;
        for (int i = 0; i < c; i++) {
            p.hub.emit(p.tag, 777);
        }
        p.hub.resetProxy(p.tag);
        latch.await();
        bh.consume(latch);
    }

    @Benchmark
    public void observe(States.ProcProxyParamsUpstream p, Blackhole bh) throws
            InterruptedException {
        int s = p.subscribers;
        CountDownLatch latch = new CountDownLatch(s);
        for (int i = 0; i < s; i++) {
            p.obs.subscribe(new LatchedSubscriber<Integer>(bh, latch));
        }

        p.upstream.connect();
        latch.await();
        bh.consume(latch);
    }
}
