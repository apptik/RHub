package io.apptik.rxhub;


import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import rx.Observable;
import rx.observables.ConnectableObservable;
import rx.subjects.PublishSubject;

public class States {

    private States() {
    }

    @State(Scope.Thread)
    public static class ProxyParamsEmit {

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

        AbstractRxJava1Hub hub;

        @Setup(Level.Iteration)
        public void setup() {
            hub = new UniJavaRx1Hub(RxJava1Hub.RxJava1ProxyType.PublishSubjectProxy);
            obs = hub.getObservable(tag);
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            hub.resetObsProxy(tag);
        }
    }

    @State(Scope.Thread)
    public static class ProxyParamsUpstream extends ProxyParamsEmit {
        public ConnectableObservable<Integer> upstream;

        @Setup(Level.Iteration)
        public void setup() {
            hub = new UniJavaRx1Hub(RxJava1Hub.RxJava1ProxyType.PublishSubjectProxy);
            obs = hub.getObservable(tag);
            upstream = Observable.range(0, count).publish();
            hub.addObsUpstream(tag, upstream);
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            hub.resetObsProxy(tag);
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
}
