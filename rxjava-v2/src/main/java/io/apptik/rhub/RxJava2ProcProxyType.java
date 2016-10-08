package io.apptik.rhub;

import io.apptik.roxy.RxJava2ProcProxy;
import io.apptik.roxy.RxJava2Proxies;


public enum RxJava2ProcProxyType implements RHub.ProxyType<RxJava2ProcProxy> {
    BehaviorProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.behaviorProcessorProxy();
        }
    },
    PublishProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.publishProcessorProxy();
        }
    },
    ReplayProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.replayProcessorProxy();
        }
    },
    SafeBehaviorProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.safeBehaviorProcessorProxy();
        }
    },
    SafePublishProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.safePublishProcessorProxy();
        }
    },
    SafeReplayProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.safeReplayProcessorProxy();
        }
    },
    SerializedBehaviorProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.serializedBehaviorProcessorProxy();
        }
    },
    SerializedPublishProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.serializedPublishProcessorProxy();
        }
    },
    SerializedReplayProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.serializedReplayProcessorProxy();
        }
    },
    SafeSerializedBehaviorProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.safeSerializedBehaviorProcessorProxy();
        }
    },
    SafeSerializedPublishProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.SafeSerializedPublishProcessorProxy();
        }
    },
    SafeSerializedReplayProcessorProxy {
        @Override
        public RxJava2ProcProxy getRoxy() {
            return RxJava2Proxies.safeSerializedReplayProcessorProxy();
        }
    }
}
