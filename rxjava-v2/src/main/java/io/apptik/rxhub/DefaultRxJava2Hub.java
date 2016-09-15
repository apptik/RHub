package io.apptik.rxhub;

public class DefaultRxJava2Hub extends AbstractRxJava2Hub {

    @Override
    public RxJava2ProxyType getProxyType(Object tag) {
        return RxJava2ProxyType.BehaviorProcessorProxy;
    }

    @Override
    public boolean isProxyThreadsafe(Object tag) {
        return true;
    }

    @Override
    public boolean canTriggerEmit(Object tag) {
        return true;
    }
}
