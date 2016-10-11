package io.apptik.rhub;

import com.jakewharton.rxrelay.*;

import io.apptik.roxy.Roxy;
import io.apptik.roxy.RxJava1Proxies;
import rx.Observable;
import rx.subjects.*;


public enum RxJava1ProxyType implements RHub.ProxyType<Roxy<Observable>> {

    /**
     * Proxy based on {@link BehaviorSubject}
     */
    BehaviorSubjectProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.behaviorSubjectProxy();
        }
    },
    /**
     * Proxy based on {@link PublishSubject}
     */
    PublishSubjectProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.publishSubjectProxy();
        }
    },
    /**
     * Proxy based on {@link ReplaySubject}
     */
    ReplaySubjectProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.replaySubjectProxy();
        }
    },
    /**
     * Proxy based on {@link BehaviorRelay}
     */
    BehaviorRelayProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.behaviorRelayProxy();
        }
    },
    /**
     * Proxy based on {@link PublishRelay}
     */
    PublishRelayProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.publishRelayProxy();
        }
    },
    /**
     * Proxy based on {@link ReplayRelay}
     */
    ReplayRelayProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.replayRelayProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link BehaviorSubject}
     */
    SerializedBehaviorSubjectProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.serializedBehaviorSubjectProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link PublishSubject}
     */
    SerializedPublishSubjectProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.serializedPublishSubjectProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link ReplaySubject}
     */
    SerializedReplaySubjectProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.serializedReplaySubjectProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link BehaviorRelay}
     */
    SerializedBehaviorRelayProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.serializedBehaviorRelayProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link PublishRelay}
     */
    SerializedPublishRelayProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.serializedPublishRelayProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link ReplayRelay}
     */
    SerializedReplayRelayProxy {
        @Override
        public Roxy<Observable> getRoxy() {
            return RxJava1Proxies.serializedReplayRelayProxy();
        }
    }
}
