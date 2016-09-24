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

import io.apptik.rxhub.AbstractRxJava1Hub;
import io.apptik.rxhub.RxJava1Hub;
import rx.Observable;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class PublishProxyPerf {

    @State(Scope.Thread)
    public static class Params {
        @Param({"1", "2", "4", "8"})
        public int subscribers;

        @Param({"1",
                "1000",
                "1000000"
        })
        public int count;

        private static final String tag = "test";

        RxJava1Hub hub = new AbstractRxJava1Hub() {
            @Override
            public RxJava1Hub.RxJava1ProxyType getProxyType(Object tag) {
                return RxJava1Hub.RxJava1ProxyType.PublishSubjectProxy;
            }

            @Override
            public boolean isProxyThreadsafe(Object tag) {
                return false;
            }

            @Override
            public boolean canTriggerEmit(Object tag) {
                return true;
            }
        };
        Observable obs = hub.getObservable(tag);
    }

    @Benchmark
    public void emit(Params p, Blackhole bh) {
        AtomicLong aLong = new AtomicLong();
        int s = p.subscribers;
        for (int i = 0; i < s; i++) {
            p.obs.subscribe(new LatchedObserver<Integer>(bh));
        }

        int c = p.count;
        for (int i = 0; i < c; i++) {
            p.hub.emit(p.tag, aLong.incrementAndGet());
        }

        p.hub.resetObsProxy(p.tag);
        bh.consume(aLong);
    }
}
