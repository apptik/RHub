package io.apptik.rhub;

import io.apptik.roxy.RxJava2Proxies;
import io.apptik.roxy.RxJava2SubjProxy;


public enum RxJava2SubjProxyType implements RHub.ProxyType<RxJava2SubjProxy> {
    BehaviorSubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.behaviorSubjectProxy();
        }
    },
    PublishSubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.publishSubjectProxy();
        }
    },
    ReplaySubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.replaySubjectProxy();
        }
    },
    SafeBehaviorSubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.safeBehaviorSubjectProxy();
        }
    },
    SafePublishSubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.safePublishSubjectProxy();
        }
    },
    SafeReplaySubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.safeReplaySubjectProxy();
        }
    },
    SerializedBehaviorSubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.serializedBehaviorSubjectProxy();
        }
    },
    SerializedPublishSubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.serializedPublishSubjectProxy();
        }
    },
    SerializedReplaySubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.serializedReplaySubjectProxy();
        }
    },
    SafeSerializedBehaviorSubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.safeSerializedBehaviorSubjectProxy();
        }
    },
    SafeSerializedPublishSubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.safeSerializedPublishSubjectProxy();
        }
    },
    SafeSerializedReplaySubjectProxy {
        @Override
        public RxJava2SubjProxy getRoxy() {
            return RxJava2Proxies.safeSerializedReplaySubjectProxy();
        }
    }
}
