package io.apptik.rxhub;


public interface ReactorHub extends RxHub {

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
