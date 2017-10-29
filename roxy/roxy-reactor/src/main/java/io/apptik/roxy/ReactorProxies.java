package io.apptik.roxy;


import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.publisher.TopicProcessor;
import reactor.core.publisher.WorkQueueProcessor;

import static io.apptik.roxy.Roxy.TePolicy.PASS;
import static io.apptik.roxy.Roxy.TePolicy.WRAP;

/**
 * Helper Roxy factories for common proxies based on Reactor-Core
 */
final public class ReactorProxies {
    private ReactorProxies() {
    }

    public static ReactorProcProxy behaviorProcessorProxy() {
        return new ReactorProcProxy(ReplayProcessor.create(1), PASS);
    }

    public static ReactorProcProxy serializedBehaviorProcessorProxy() {
        return new ReactorProcProxy(ReplayProcessor.create(1).serialize(), PASS);
    }

    public static ReactorProcProxy emitterProcessorProxy() {
        //todo .connect() later when first subscribed to
        return new ReactorProcProxy(EmitterProcessor.create(false), PASS);
    }

    public static ReactorProcProxy serializedEmitterProcessorProxy() {
        return new ReactorProcProxy(EmitterProcessor.create(false).serialize(), PASS);
    }

    public static ReactorProcProxy topicProcessorProxy() {
        return new ReactorProcProxy(TopicProcessor.create(), PASS);
    }

    public static ReactorProcProxy serializedTopicProcessorProxy() {
        return new ReactorProcProxy(TopicProcessor.create().serialize(), PASS);
    }

    public static ReactorProcProxy replayProcessorProxy() {
        return new ReactorProcProxy(ReplayProcessor.create(), PASS);
    }

    public static ReactorProcProxy serializedReplayProcessorProxy() {
        return new ReactorProcProxy(ReplayProcessor.create().serialize(), PASS);
    }

    public static ReactorProcProxy workQueueProcessorProxy() {
        return new ReactorProcProxy(WorkQueueProcessor.create(), PASS);
    }

    public static ReactorProcProxy serializedWorkQueueProcessorProxy() {
        return new ReactorProcProxy(WorkQueueProcessor.create().serialize(), PASS);
    }

    public static ReactorProcProxy safeBehaviorProcessorProxy() {
        return new ReactorProcProxy(ReplayProcessor.create(1), WRAP);
    }

    public static ReactorProcProxy safeSerializedBehaviorProcessorProxy() {
        return new ReactorProcProxy(ReplayProcessor.create(1).serialize(), WRAP);
    }

    public static ReactorProcProxy safeEmitterProcessorProxy() {
        return new ReactorProcProxy(EmitterProcessor.create(false), WRAP);
    }

    public static ReactorProcProxy safeSerializedEmitterProcessorProxy() {
        return new ReactorProcProxy(EmitterProcessor.create(false).serialize(), WRAP);
    }

    public static ReactorProcProxy safeTopicProcessorProxy() {
        return new ReactorProcProxy(TopicProcessor.create(), WRAP);
    }

    public static ReactorProcProxy safeSerializedTopicProcessorProxy() {
        return new ReactorProcProxy(TopicProcessor.create().serialize(), WRAP);
    }

    public static ReactorProcProxy safeReplayProcessorProxy() {
        return new ReactorProcProxy(ReplayProcessor.create(), WRAP);
    }

    public static ReactorProcProxy safeSerializedReplayProcessorProxy() {
        return new ReactorProcProxy(ReplayProcessor.create().serialize(), WRAP);
    }

    public static ReactorProcProxy safeWorkQueueProcessorProxy() {
        return new ReactorProcProxy(WorkQueueProcessor.create(), WRAP);
    }

    public static ReactorProcProxy safeSerializedWorkQueueProcessorProxy() {
        return new ReactorProcProxy(WorkQueueProcessor.create().serialize(), WRAP);
    }


}

