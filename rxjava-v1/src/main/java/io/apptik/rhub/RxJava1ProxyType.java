package io.apptik.rhub;

import io.apptik.roxy.Roxy;
import io.apptik.roxy.RxJava1Proxies;
import rx.Observable;


public enum RxJava1ProxyType implements RHub.ProxyType<Roxy<Observable>> {
    BehaviorSubjectProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.behaviorSubjectProxy();
        }
    },
    PublishSubjectProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.publishSubjectProxy();
        }
    },
    ReplaySubjectProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.replaySubjectProxy();
        }
    },
    BehaviorRelayProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.behaviorRelayProxy();
        }
    },
    PublishRelayProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.publishRelayProxy();
        }
    },
    ReplayRelayProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.replayRelayProxy();
        }
    },

    SerializedBehaviorSubjectProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.serializedBehaviorSubjectProxy();
        }
    },
    SerializedPublishSubjectProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.serializedPublishSubjectProxy();
        }
    },
    SerializedReplaySubjectProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.serializedReplaySubjectProxy();
        }
    },
    SerializedBehaviorRelayProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.serializedBehaviorRelayProxy();
        }
    },
    SerializedPublishRelayProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.serializedPublishRelayProxy();
        }
    },
    SerializedReplayRelayProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.serializedReplayRelayProxy();
        }
    }
}
