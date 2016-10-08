package io.apptik.roxy;

import com.jakewharton.rxrelay.BehaviorRelay;
import com.jakewharton.rxrelay.PublishRelay;
import com.jakewharton.rxrelay.ReplayRelay;

import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;

public class RxJava1Proxies {

    private RxJava1Proxies() {
    }

    public static RxJava1SubjProxy behaviorSubjectProxy() {
        return new RxJava1SubjProxy(BehaviorSubject.create());
    }

    public static RxJava1SubjProxy publishSubjectProxy() {
        return new RxJava1SubjProxy(PublishSubject.create());
    }

    public static RxJava1SubjProxy replaySubjectProxy() {
        return new RxJava1SubjProxy(ReplaySubject.create());
    }

    public static RxJava1RelayProxy behaviorRelayProxy() {
        return new RxJava1RelayProxy(BehaviorRelay.create());
    }

    public static RxJava1RelayProxy publishRelayProxy() {
        return new RxJava1RelayProxy(PublishRelay.create());
    }

    public static RxJava1RelayProxy replayRelayProxy() {
        return new RxJava1RelayProxy(ReplayRelay.create());
    }

    public static RxJava1SubjProxy serializedBehaviorSubjectProxy() {
        return new RxJava1SubjProxy(BehaviorSubject.create().toSerialized());
    }

    public static RxJava1SubjProxy serializedPublishSubjectProxy() {
        return new RxJava1SubjProxy(PublishSubject.create().toSerialized());
    }

    public static RxJava1SubjProxy serializedReplaySubjectProxy() {
        return new RxJava1SubjProxy(ReplaySubject.create().toSerialized());
    }

    public static RxJava1RelayProxy serializedBehaviorRelayProxy() {
        return new RxJava1RelayProxy(BehaviorRelay.create().toSerialized());
    }

    public static RxJava1RelayProxy serializedPublishRelayProxy() {
        return new RxJava1RelayProxy(PublishRelay.create().toSerialized());
    }

    public static RxJava1RelayProxy serializedReplayRelayProxy() {
        return new RxJava1RelayProxy(ReplayRelay.create().toSerialized());
    }
}
