package io.apptik.rhub;


import io.apptik.rhub.RHub.ProxyType;
import io.apptik.roxy.ReactorProcProxy;
import io.apptik.roxy.ReactorProxies;
import io.apptik.roxy.Roxy.Event;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.publisher.TopicProcessor;
import reactor.core.publisher.WorkQueueProcessor;

enum ReactorProxyType implements ProxyType<ReactorProcProxy> {

    /**
     * Proxy based on {@link EmitterProcessor}
     */
    EmitterProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.emitterProcessorProxy();
        }
    },
    /**
     * Proxy based on {@link ReplayProcessor} with buffer of 1 element
     */
    BehaviorProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.behaviorProcessorProxy();
        }
    },
    /**
     * Proxy based on {@link ReplayProcessor}
     */
    ReplayProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.replayProcessorProxy();
        }
    },
    /**
     * Proxy based on {@link TopicProcessor}
     */
    TopicProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.topicProcessorProxy();
        }
    },
    /**
     * Proxy based on {@link WorkQueueProcessor}
     */
    WorkQueueProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.workQueueProcessorProxy();
        }
    },
    /**
     * Proxy based on {@link EmitterProcessor} where error and complete events will be received
     * wrapped in
     * {@link Event}
     */
    SafeEmitterProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.safeEmitterProcessorProxy();
        }
    },
    /**
     * Proxy based on {@link ReplayProcessor} with buffer of 1 element where error and complete
     * events will be received wrapped in {@link Event}
     */
    SafeBehaviorProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.safeBehaviorProcessorProxy();
        }
    },
    /**
     * Proxy based on {@link ReplayProcessor} where error and complete events will be received
     * wrapped in {@link Event}
     */
    SafeReplayProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.safeReplayProcessorProxy();
        }
    },
    /**
     * Proxy based on {@link TopicProcessor} where error and complete events will be received
     * wrapped in
     * {@link Event}
     */
    SafeTopicProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.safeTopicProcessorProxy();
        }
    },
    /**
     * Proxy based on {@link WorkQueueProcessor} where error and complete events will be received
     * wrapped in
     * {@link Event}
     */
    SafeWorkQueueProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.safeWorkQueueProcessorProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link EmitterProcessor}
     */
    SerializedEmitterProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.serializedEmitterProcessorProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link ReplayProcessor}
     */
    SerializedBehaviorProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.serializedBehaviorProcessorProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link ReplayProcessor}
     */
    SerializedReplayProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.serializedReplayProcessorProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link TopicProcessor}
     */
    SerializedTopicProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.serializedTopicProcessorProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link WorkQueueProcessor}
     */
    SerializedWorkQueueProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.serializedWorkQueueProcessorProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link EmitterProcessor} where error and complete events will
     * be received
     * wrapped in {@link Event}
     */
    SafeSerializedEmitterProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.safeSerializedEmitterProcessorProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link ReplayProcessor} where error and complete events will
     * be received
     * wrapped in {@link Event}
     */
    SafeSerializedBehaviorProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.safeSerializedBehaviorProcessorProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link ReplayProcessor} where error and complete events will
     * be received
     * wrapped in {@link Event}
     */
    SafeSerializedReplayProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.safeSerializedReplayProcessorProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link ReplayProcessor} where error and complete events will
     * be received
     * wrapped in {@link Event}
     */
    SafeSerializedTopicProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.safeSerializedTopicProcessorProxy();
        }
    },
    /**
     * Synchronized Proxy based on {@link ReplayProcessor} where error and complete events will
     * be received
     * wrapped in {@link Event}
     */
    SafeSerializedWorkQueueProcessorProxy {
        @Override
        public ReactorProcProxy getRoxy() {
            return ReactorProxies.safeSerializedWorkQueueProcessorProxy();
        }
    }
}
