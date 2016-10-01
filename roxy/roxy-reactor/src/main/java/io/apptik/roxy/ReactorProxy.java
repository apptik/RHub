package io.apptik.roxy;


import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.publisher.TopicProcessor;
import reactor.core.publisher.WorkQueueProcessor;

import static io.apptik.roxy.Roxy.TePolicy.PASS;
import static io.apptik.roxy.Roxy.TePolicy.WRAP;

final public class ReactorProxy {
    private ReactorProxy() {
    }

    public static RSProxy BehaviorProcessorProxy() {
        return new RSProxy(ReplayProcessor.create(1), PASS);
    }

    public static RSProxy SerializedBehaviorProcessorProxy() {
        return new RSProxy(ReplayProcessor.create(1).serialize(), PASS);
    }

    public static RSProxy EmitterProcessorProxy() {
        return new RSProxy(EmitterProcessor.create(), PASS);
    }

    public static RSProxy SerializedEmitterProcessorProxy() {
        return new RSProxy(EmitterProcessor.create().serialize(), PASS);
    }

    public static RSProxy TopicProcessorProxy() {
        return new RSProxy(TopicProcessor.create(), PASS);
    }

    public static RSProxy SerializedTopicProcessorProxy() {
        return new RSProxy(TopicProcessor.create().serialize(), PASS);
    }

    public static RSProxy ReplayProcessorProxy() {
        return new RSProxy(ReplayProcessor.create(), PASS);
    }

    public static RSProxy SerializedReplayProcessorProxy() {
        return new RSProxy(ReplayProcessor.create().serialize(), PASS);
    }

    public static RSProxy WorkQueueProcessorProxy() {
        return new RSProxy(WorkQueueProcessor.create(), PASS);
    }

    public static RSProxy SerializedWorkQueueProcessorProxy() {
        return new RSProxy(WorkQueueProcessor.create().serialize(), PASS);
    }

    public static RSProxy SafeBehaviorProcessorProxy() {
        return new RSProxy(ReplayProcessor.create(1), WRAP);
    }

    public static RSProxy SafeSerializedBehaviorProcessorProxy() {
        return new RSProxy(ReplayProcessor.create(1).serialize(), WRAP);
    }

    public static RSProxy SafeEmitterProcessorProxy() {
        return new RSProxy(EmitterProcessor.create(), WRAP);
    }

    public static RSProxy SafeSerializedEmitterProcessorProxy() {
        return new RSProxy(EmitterProcessor.create().serialize(), WRAP);
    }

    public static RSProxy SafeTopicProcessorProxy() {
        return new RSProxy(TopicProcessor.create(), WRAP);
    }

    public static RSProxy SafeSerializedTopicProcessorProxy() {
        return new RSProxy(TopicProcessor.create().serialize(), WRAP);
    }

    public static RSProxy SafeReplayProcessorProxy() {
        return new RSProxy(ReplayProcessor.create(), WRAP);
    }

    public static RSProxy SafeSerializedReplayProcessorProxy() {
        return new RSProxy(ReplayProcessor.create().serialize(), WRAP);
    }

    public static RSProxy SafeWorkQueueProcessorProxy() {
        return new RSProxy(WorkQueueProcessor.create(), WRAP);
    }

    public static RSProxy SafeSerializedWorkQueueProcessorProxy() {
        return new RSProxy(WorkQueueProcessor.create().serialize(), WRAP);
    }


}

