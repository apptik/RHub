package io.apptik.rxhub;


public interface ReactorHub extends RxHub {

    enum ReactorNodeType implements ProxyType {
        EmitterProcessor,
        ReplayProcessor,
        TopicProcessor,
        WorkQueueProcessor
    }
}
