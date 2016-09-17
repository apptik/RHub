package io.apptik.rxhub;


import reactor.core.publisher.Flux;

public interface ReactorHub extends RxHub<Flux> {

    enum ReactorNodeType implements ProxyType {
        EmitterProcessor,
        ReplayProcessor,
        TopicProcessor,
        WorkQueueProcessor
    }
}
