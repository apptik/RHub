package io.apptik.rhub;

import io.apptik.roxy.Roxy;
import io.apptik.roxy.RxJava2Proxies;
import io.apptik.roxy.RxJava2SubjProxy;
import io.reactivex.subjects.*;


public enum RxJava2SubjProxyType implements RHub.ProxyType<RxJava2SubjProxy> {

    /**
     * Proxy based on {@link BehaviorSubject}
     */
    BehaviorSubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.behaviorSubjectProxy();
        }
    },
    /**
     * Proxy based on {@link PublishSubject}
     */
    PublishSubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.publishSubjectProxy();
        }
    },
    /**
     * Proxy based on {@link ReplaySubject}
     */
    ReplaySubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.replaySubjectProxy();
        }
    },
    /**
     * Proxy based on {@link BehaviorSubject} where error and complete events will be received
     * wrapped in {@link Roxy.Event}
     */
    SafeBehaviorSubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.safeBehaviorSubjectProxy();
        }
    },
    /**
     * Proxy based on {@link PublishSubject} where error and complete events will be received
     * wrapped in {@link Roxy.Event}
     */
    SafePublishSubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.safePublishSubjectProxy();
        }
    },
    /**
     * Proxy based on {@link ReplaySubject} where error and complete events will be received
     * wrapped in {@link Roxy.Event}
     */
    SafeReplaySubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.safeReplaySubjectProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link BehaviorSubject}
     */
    SerializedBehaviorSubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.serializedBehaviorSubjectProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link PublishSubject}
     */
    SerializedPublishSubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.serializedPublishSubjectProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link ReplaySubject}
     */
    SerializedReplaySubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.serializedReplaySubjectProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link BehaviorSubject} where error and complete events will be received
     * wrapped in {@link Roxy.Event}
     */
    SafeSerializedBehaviorSubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.safeSerializedBehaviorSubjectProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link PublishSubject} where error and complete events will be received
     * wrapped in {@link Roxy.Event}
     */
    SafeSerializedPublishSubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.safeSerializedPublishSubjectProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link ReplaySubject} where error and complete events will be received
     * wrapped in {@link Roxy.Event}
     */
    SafeSerializedReplaySubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.safeSerializedReplaySubjectProxy();
        }
    }
}
