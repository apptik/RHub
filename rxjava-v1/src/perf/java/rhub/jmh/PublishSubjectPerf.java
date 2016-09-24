package rhub.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import rx.subjects.PublishSubject;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class PublishSubjectPerf {

    @State(Scope.Thread)
    public static class Params {
        @Param({"1", "2", "4", "8"})
        public int subscribers;

        @Param({"1",
                "1000",
                "1000000"
        })
        public int count;

        public PublishSubject<Long> ps = PublishSubject.create();
    }

    @Benchmark
    public void benchmark(Params p, Blackhole bh) {
        AtomicLong aLong = new AtomicLong();

        int s = p.subscribers;
        for (int i = 0; i < s; i++) {
            p.ps.subscribe(new LatchedObserver<>(bh));
        }

        int c = p.count;
        for (int i = 0; i < c; i++) {
            p.ps.onNext(aLong.incrementAndGet());
        }

        p.ps.onCompleted();
        bh.consume(aLong);
    }
}
