package io.apptik.roxy;


import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.processors.ReplayProcessor;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;

/**
 * Helper Roxy factories for common proxies based on RxJava 2.x
 */
public final class RxJava2Proxies {

    private RxJava2Proxies(){};

    public static RxJava2SubjProxy behaviorSubjectProxy() {
        return new RxJava2SubjProxy(BehaviorSubject.create(), Roxy.TePolicy.PASS);
    }
    public static RxJava2SubjProxy serializedBehaviorSubjectProxy() {
        return new RxJava2SubjProxy(BehaviorSubject.create().toSerialized(), Roxy.TePolicy.PASS);
    }
    public static RxJava2SubjProxy publishSubjectProxy() {
        return new RxJava2SubjProxy(PublishSubject.create(), Roxy.TePolicy.PASS);
    }
    public static RxJava2SubjProxy serializedPublishSubjectProxy() {
        return new RxJava2SubjProxy(PublishSubject.create().toSerialized(), Roxy.TePolicy.PASS);
    }
    public static RxJava2SubjProxy replaySubjectProxy() {
        return new RxJava2SubjProxy(ReplaySubject.create(), Roxy.TePolicy.PASS);
    }
    public static RxJava2SubjProxy serializedReplaySubjectProxy() {
        return new RxJava2SubjProxy(ReplaySubject.create().toSerialized(), Roxy.TePolicy.PASS);
    }
    public static RxJava2SubjProxy safeBehaviorSubjectProxy() {
        return new RxJava2SubjProxy(BehaviorSubject.create(), Roxy.TePolicy.WRAP);
    }
    public static RxJava2SubjProxy safeSerializedBehaviorSubjectProxy() {
        return new RxJava2SubjProxy(BehaviorSubject.create().toSerialized(), Roxy.TePolicy.WRAP);
    }
    public static RxJava2SubjProxy safePublishSubjectProxy() {
        return new RxJava2SubjProxy(PublishSubject.create(), Roxy.TePolicy.WRAP);
    }
    public static RxJava2SubjProxy safeSerializedPublishSubjectProxy() {
        return new RxJava2SubjProxy(PublishSubject.create().toSerialized(), Roxy.TePolicy.WRAP);
    }
    public static RxJava2SubjProxy safeReplaySubjectProxy() {
        return new RxJava2SubjProxy(ReplaySubject.create(), Roxy.TePolicy.WRAP);
    }
    public static RxJava2SubjProxy safeSerializedReplaySubjectProxy() {
        return new RxJava2SubjProxy(ReplaySubject.create().toSerialized(), Roxy.TePolicy.WRAP);
    }

    public static RxJava2ProcProxy behaviorProcessorProxy() {
        return new RxJava2ProcProxy(BehaviorProcessor.create(), Roxy.TePolicy.PASS);
    }
    public static RxJava2ProcProxy serializedBehaviorProcessorProxy() {
        return new RxJava2ProcProxy(BehaviorProcessor.create().toSerialized(), Roxy.TePolicy.PASS);
    }
    public static RxJava2ProcProxy publishProcessorProxy() {
        return new RxJava2ProcProxy(PublishProcessor.create(), Roxy.TePolicy.PASS);
    }
    public static RxJava2ProcProxy serializedPublishProcessorProxy() {
        return new RxJava2ProcProxy(PublishProcessor.create().toSerialized(), Roxy.TePolicy.PASS);
    }
    public static RxJava2ProcProxy replayProcessorProxy() {
        return new RxJava2ProcProxy(ReplayProcessor.create(), Roxy.TePolicy.PASS);
    }
    public static RxJava2ProcProxy serializedReplayProcessorProxy() {
        return new RxJava2ProcProxy(ReplayProcessor.create().toSerialized(), Roxy.TePolicy.PASS);
    }
    public static RxJava2ProcProxy safeBehaviorProcessorProxy() {
        return new RxJava2ProcProxy(BehaviorProcessor.create(), Roxy.TePolicy.WRAP);
    }
    public static RxJava2ProcProxy safeSerializedBehaviorProcessorProxy() {
        return new RxJava2ProcProxy(BehaviorProcessor.create().toSerialized(), Roxy.TePolicy.WRAP);
    }
    public static RxJava2ProcProxy safePublishProcessorProxy() {
        return new RxJava2ProcProxy(PublishProcessor.create(), Roxy.TePolicy.WRAP);
    }
    public static RxJava2ProcProxy SafeSerializedPublishProcessorProxy() {
        return new RxJava2ProcProxy(PublishProcessor.create().toSerialized(), Roxy.TePolicy.WRAP);
    }
    public static RxJava2ProcProxy safeReplayProcessorProxy() {
        return new RxJava2ProcProxy(ReplayProcessor.create(), Roxy.TePolicy.WRAP);
    }
    public static RxJava2ProcProxy safeSerializedReplayProcessorProxy() {
        return new RxJava2ProcProxy(ReplayProcessor.create().toSerialized(), Roxy.TePolicy.WRAP);
    }


}
