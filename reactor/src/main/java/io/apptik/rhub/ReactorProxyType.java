package io.apptik.rhub;


import io.apptik.rhub.RHub.ProxyType;
import io.apptik.roxy.ReactorProcProxy;
import io.apptik.roxy.ReactorProxies;

enum ReactorProxyType implements ProxyType<ReactorProcProxy> {
    EmitterProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.emitterProcessorProxy();
        }
    },
    BehaviorProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.behaviorProcessorProxy();
        }
    },
    ReplayProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.replayProcessorProxy();
        }
    },
    TopicProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.topicProcessorProxy();
        }
    },
    WorkQueueProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.workQueueProcessorProxy();
        }
    },
    SafeEmitterProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.safeEmitterProcessorProxy();
        }
    },
    SafeBehaviorProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.safeBehaviorProcessorProxy();
        }
    },
    SafeReplayProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.safeReplayProcessorProxy();
        }
    },
    SafeTopicProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.safeTopicProcessorProxy();
        }
    },
    SafeWorkQueueProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.safeWorkQueueProcessorProxy();
        }
    },
    SerializedEmitterProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.serializedEmitterProcessorProxy();
        }
    },
    SerializedBehaviorProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.serializedBehaviorProcessorProxy();
        }
    },
    SerializedReplayProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.serializedReplayProcessorProxy();
        }
    },
    SerializedTopicProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.serializedTopicProcessorProxy();
        }
    },
    SerializedWorkQueueProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.serializedWorkQueueProcessorProxy();
        }
    },
    SafeSerializedEmitterProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.safeSerializedEmitterProcessorProxy();
        }
    },
    SafeSerializedBehaviorProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.safeSerializedBehaviorProcessorProxy();
        }
    },
    SafeSerializedReplayProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.safeSerializedReplayProcessorProxy();
        }
    },
    SafeSerializedTopicProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.safeSerializedTopicProcessorProxy();
        }
    },
    SafeSerializedWorkQueueProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.safeSerializedWorkQueueProcessorProxy();
        }
    }
}
