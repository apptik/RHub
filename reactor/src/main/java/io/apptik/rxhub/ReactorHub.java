package io.apptik.rxhub;


public interface ReactorHub extends RxHub {

    enum ReactorNodeType implements NodeType {
        EmitterProcessor,
        ReplayProcessor,
        TopicProcessor,
        WorkQueueProcessor
    }
}
