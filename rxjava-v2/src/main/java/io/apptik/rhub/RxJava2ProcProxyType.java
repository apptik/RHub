package io.apptik.rhub;

import io.apptik.roxy.Roxy;
import io.apptik.roxy.RxJava2ProcProxy;
import io.apptik.roxy.RxJava2Proxies;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.processors.ReplayProcessor;


public enum RxJava2ProcProxyType implements RHub.ProxyType<RxJava2ProcProxy> {
    /**
     * Proxy based on {@link BehaviorProcessor}
     */
    BehaviorProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.behaviorProcessorProxy();
        }
    },
    /**
     * Proxy based on {@link PublishProcessor}
     */
    PublishProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.publishProcessorProxy();
        }
    },
    /**
     * Proxy based on {@link ReplayProcessor}
     */
    ReplayProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.replayProcessorProxy();
        }
    },
    /**
     * Proxy based on {@link BehaviorProcessor} where error and complete events will be received
     * wrapped in {@link Roxy.Event}
     */
    SafeBehaviorProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.safeBehaviorProcessorProxy();
        }
    },
    /**
     * Proxy based on {@link PublishProcessor} where error and complete events will be received
     * wrapped in {@link Roxy.Event}
     */
    SafePublishProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.safePublishProcessorProxy();
        }
    },
    /**
     * Proxy based on {@link ReplayProcessor} where error and complete events will be received
     * wrapped in {@link Roxy.Event}
     */
    SafeReplayProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.safeReplayProcessorProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link BehaviorProcessor}
     */
    SerializedBehaviorProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.serializedBehaviorProcessorProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link PublishProcessor}
     */
    SerializedPublishProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.serializedPublishProcessorProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link ReplayProcessor}
     */
    SerializedReplayProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.serializedReplayProcessorProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link BehaviorProcessor} where error and complete events will
     * be received
     * wrapped in {@link Roxy.Event}
     */
    SafeSerializedBehaviorProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.safeSerializedBehaviorProcessorProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link PublishProcessor} where error and complete events will
     * be received
     * wrapped in {@link Roxy.Event}
     */
    SafeSerializedPublishProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.SafeSerializedPublishProcessorProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link ReplayProcessor} where error and complete events will
     * be received
     * wrapped in {@link Roxy.Event}
     */
    SafeSerializedReplayProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.safeSerializedReplayProcessorProxy();
        }
    }
}
