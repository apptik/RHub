package io.apptik.roxy;


import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.processors.ReplayProcessor;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;

public final class RxJava2Proxy {

    private RxJava2Proxy(){};

    public static SubjProxy BehaviorSubjectProxy() {
        return new SubjProxy(BehaviorSubject.create(), Roxy.TePolicy.PASS);
    }
    public static SubjProxy SerializedBehaviorSubjectProxy() {
        return new SubjProxy(BehaviorSubject.create().toSerialized(), Roxy.TePolicy.PASS);
    }
    public static SubjProxy PublishSubjectProxy() {
        return new SubjProxy(PublishSubject.create(), Roxy.TePolicy.PASS);
    }
    public static SubjProxy SerializedPublishSubjectProxy() {
        return new SubjProxy(PublishSubject.create().toSerialized(), Roxy.TePolicy.PASS);
    }
    public static SubjProxy ReplaySubjectProxy() {
        return new SubjProxy(ReplaySubject.create(), Roxy.TePolicy.PASS);
    }
    public static SubjProxy SerializedReplaySubjectProxy() {
        return new SubjProxy(ReplaySubject.create().toSerialized(), Roxy.TePolicy.PASS);
    }
    public static SubjProxy SafeBehaviorSubjectProxy() {
        return new SubjProxy(BehaviorSubject.create(), Roxy.TePolicy.WRAP);
    }
    public static SubjProxy SafeSerializedBehaviorSubjectProxy() {
        return new SubjProxy(BehaviorSubject.create().toSerialized(), Roxy.TePolicy.WRAP);
    }
    public static SubjProxy SafePublishSubjectProxy() {
        return new SubjProxy(PublishSubject.create(), Roxy.TePolicy.WRAP);
    }
    public static SubjProxy SafeSerializedPublishSubjectProxy() {
        return new SubjProxy(PublishSubject.create().toSerialized(), Roxy.TePolicy.WRAP);
    }
    public static SubjProxy SafeReplaySubjectProxy() {
        return new SubjProxy(ReplaySubject.create(), Roxy.TePolicy.WRAP);
    }
    public static SubjProxy SafeSerializedReplaySubjectProxy() {
        return new SubjProxy(ReplaySubject.create().toSerialized(), Roxy.TePolicy.WRAP);
    }

    public static RSProxy BehaviorProcessorProxy() {
        return new RSProxy(BehaviorProcessor.create(), Roxy.TePolicy.PASS);
    }
    public static RSProxy SerializedBehaviorProcessorProxy() {
        return new RSProxy(BehaviorProcessor.create().toSerialized(), Roxy.TePolicy.PASS);
    }
    public static RSProxy PublishProcessorProxy() {
        return new RSProxy(PublishProcessor.create(), Roxy.TePolicy.PASS);
    }
    public static RSProxy SerializedPublishProcessorProxy() {
        return new RSProxy(PublishProcessor.create().toSerialized(), Roxy.TePolicy.PASS);
    }
    public static RSProxy ReplayProcessorProxy() {
        return new RSProxy(ReplayProcessor.create(), Roxy.TePolicy.PASS);
    }
    public static RSProxy SerializedReplayProcessorProxy() {
        return new RSProxy(ReplayProcessor.create().toSerialized(), Roxy.TePolicy.PASS);
    }
    public static RSProxy SafeBehaviorProcessorProxy() {
        return new RSProxy(BehaviorProcessor.create(), Roxy.TePolicy.WRAP);
    }
    public static RSProxy SafeSerializedBehaviorProcessorProxy() {
        return new RSProxy(BehaviorProcessor.create().toSerialized(), Roxy.TePolicy.WRAP);
    }
    public static RSProxy SafePublishProcessorProxy() {
        return new RSProxy(PublishProcessor.create(), Roxy.TePolicy.WRAP);
    }
    public static RSProxy SafeSerializedPublishProcessorProxy() {
        return new RSProxy(PublishProcessor.create().toSerialized(), Roxy.TePolicy.WRAP);
    }
    public static RSProxy SafeReplayProcessorProxy() {
        return new RSProxy(ReplayProcessor.create(), Roxy.TePolicy.WRAP);
    }
    public static RSProxy SafeSerializedReplayProcessorProxy() {
        return new RSProxy(ReplayProcessor.create().toSerialized(), Roxy.TePolicy.WRAP);
    }


}
