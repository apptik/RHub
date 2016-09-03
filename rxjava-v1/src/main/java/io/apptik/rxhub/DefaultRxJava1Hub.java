package io.apptik.rxhub;

public class DefaultRxJava1Hub extends AbstractRxJava1Hub {

    @Override
    public NodeType getNodeType(Object tag) {
        return NodeType.BehaviorRelay;
    }

    @Override
    public boolean isNodeThreadsafe(Object tag) {
        return true;
    }

    @Override
    public boolean canTriggerEmit(Object tag) {
        return true;
    }
}
