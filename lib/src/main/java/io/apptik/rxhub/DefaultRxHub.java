package io.apptik.rxhub;

public class DefaultRxHub extends AbstractRxHub {

    @Override
    public NodeType getNodeType(Object tag) {
        return NodeType.BehaviorRelay;
    }

    @Override
    public boolean isNodeThreadsafe(Object tag) {
        return true;
    }
}
