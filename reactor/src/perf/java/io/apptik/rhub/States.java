package io.apptik.rhub;


import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.reactivestreams.Publisher;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import static io.apptik.rhub.ReactorProxyTypes.ReactorProxyType.EmitterProcessorProxy;

public class States {

    private States() {
    }


    @State(Scope.Thread)
    public static class ProcProxyParamsEmit {

        @Param({"1", "2", "4", "8"})
        public int subscribers;

        @Param({
                "1",
                "1000",
                "1000000"
        })
        public int count;

        public static final String tag = "test";

        public Publisher obs;

        AbstractReactorHub hub;

        @Setup(Level.Iteration)
        public void setup() {
            hub = new UniReactorHub(EmitterProcessorProxy);
            obs = hub.getPub(tag);
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            hub.resetProxy(tag);
        }
    }

    @State(Scope.Thread)
    public static class ProcProxyParamsUpstream extends ProcProxyParamsEmit {
        public ConnectableFlux<Integer> upstream;

        @Setup(Level.Iteration)
        public void setup() {
            hub = new UniReactorHub(EmitterProcessorProxy);
            obs = hub.getPub(tag);
            upstream = Flux.range(0, count).publish();
            hub.addUpstream(tag, upstream);
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            hub.resetProxy(tag);
        }
    }

    @State(Scope.Thread)
    public static class ProcessorParamsEmit {
        @Param({"1",
                "2", "4", "8"
        })
        public int subscribers;

        @Param({
                "1",
                "1000",
                "1000000"
        })
        public int count;

        public EmitterProcessor<Integer> ps;

        @Setup(Level.Iteration)
        public void setup() {
            ps = EmitterProcessor.create();
            ps.connect();
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            //no need
        }
    }

    @State(Scope.Thread)
    public static class ProcessorParamsUpstream extends ProcessorParamsEmit {
        public ConnectableFlux<Integer> upstream;

        @Setup(Level.Iteration)
        public void setup() {
            ps = EmitterProcessor.create();
            upstream = Flux.range(0, count).publish();
            upstream.subscribe(ps);
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            //no need
        }
    }
}
