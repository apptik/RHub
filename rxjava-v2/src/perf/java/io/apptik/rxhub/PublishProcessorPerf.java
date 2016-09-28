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
public class PublishProcessorPerf {

    //@Benchmark
    public void onNext(States.ProcessorParamsEmit p, Blackhole bh) throws InterruptedException {
        int s = p.subscribers;
        CountDownLatch latch = new CountDownLatch(s);
        for (int i = 0; i < s; i++) {
            p.ps.subscribe(new LatchedSubscriber(bh, latch));
        }

        int c = p.count;
        for (int i = 0; i < c; i++) {
            p.ps.onNext(i);
        }
        p.ps.onComplete();
        latch.await();
        bh.consume(latch);
    }

    //@Benchmark
    public void observe(States.ProcessorParamsUpstream p, Blackhole bh) throws
            InterruptedException {
        int s = p.subscribers;
        CountDownLatch latch = new CountDownLatch(s);
        for (int i = 0; i < s; i++) {
            p.ps.subscribe(new LatchedSubscriber<>(bh, latch));
        }

        p.upstream.connect();
        latch.await();
        bh.consume(latch);
    }
}
