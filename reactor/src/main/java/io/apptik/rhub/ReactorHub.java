package io.apptik.rhub;


public interface ReactorHub extends RSHub {

    enum ReactorProxyType implements ProxyType {
        EmitterProcessorProxy,
        BehaviorProcessorProxy,
        ReplayProcessorProxy,
        TopicProcessorProxy,
        WorkQueueProcessorProxy,
        EmitterSafeProxy,
        BehaviorSafeProxy,
        ReplaySafeProxy,
        TopicSafeProxy,
        WorkQueueSafeProxy
    }
}
