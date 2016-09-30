package io.apptik.rhub;


import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subjects.PublishSubject;

import static io.apptik.rhub.RxJava2ObsHub.RxJava2ObsProxyType.PublishSubjectProxy;
import static io.apptik.rhub.RxJava2ObsHub.RxJava2PubProxyType.PublishProcessorProxy;

public class States {

    private States() {
    }

    @State(Scope.Thread)
    public static class ObjProxyParamsEmit {

        @Param({"1", "2", "4", "8"})
        public int subscribers;

        @Param({
                "1",
                "1000",
                "1000000"
        })
        public int count;

        public static final String tag = "test";

        public Observable obs;

        AbstractRxJava2ObsHub hub;

        @Setup(Level.Iteration)
        public void setup() {
            hub = new UniJavaRx2ObsHub(PublishSubjectProxy);
            obs = hub.getPub(tag);
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            hub.resetProxy(tag);
        }
    }

    @State(Scope.Thread)
    public static class ObjProxyParamsUpstream extends ObjProxyParamsEmit {
        public ConnectableObservable<Integer> upstream;

        @Setup(Level.Iteration)
        public void setup() {
            hub = new UniJavaRx2ObsHub(PublishSubjectProxy);
            obs = hub.getPub(tag);
            upstream = Observable.range(0, count).publish();
            hub.addUpstream(tag, upstream);
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            hub.resetProxy(tag);
        }
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

        AbstractRxJava2PubHub hub;

        @Setup(Level.Iteration)
        public void setup() {
            hub = new UniJavaRx2PubHub(PublishProcessorProxy);
            obs = hub.getPub(tag);
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            hub.resetProxy(tag);
        }
    }

    @State(Scope.Thread)
    public static class ProcProxyParamsUpstream extends ProcProxyParamsEmit {
        public ConnectableFlowable<Integer> upstream;

        @Setup(Level.Iteration)
        public void setup() {
            hub = new UniJavaRx2PubHub(PublishProcessorProxy);
            obs = hub.getPub(tag);
            upstream = Flowable.range(0, count).publish();
            hub.addUpstream(tag, upstream);
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            hub.resetProxy(tag);
        }
    }

    @State(Scope.Thread)
    public static class SubjectParamsEmit {
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

        public PublishSubject<Integer> ps;

        @Setup(Level.Iteration)
        public void setup() {
            ps = PublishSubject.create();
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            //no need
        }
    }

    @State(Scope.Thread)
    public static class SubjectParamsUpstream extends SubjectParamsEmit {
        public ConnectableObservable<Integer> upstream;

        @Setup(Level.Iteration)
        public void setup() {
            ps = PublishSubject.create();
            upstream = Observable.range(0, count).publish();
            upstream.subscribe(ps);
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            //no need
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

        public PublishProcessor<Integer> ps;

        @Setup(Level.Iteration)
        public void setup() {
            ps = PublishProcessor.create();
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            //no need
        }
    }

    @State(Scope.Thread)
    public static class ProcessorParamsUpstream extends ProcessorParamsEmit {
        public ConnectableFlowable<Integer> upstream;

        @Setup(Level.Iteration)
        public void setup() {
            ps = PublishProcessor.create();
            upstream = Flowable.range(0, count).publish();
            upstream.subscribe(ps);
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            //no need
        }
    }
}
