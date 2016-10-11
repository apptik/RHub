package io.apptik.rhub;

/**
 * Default implementation of {@link RxJava1Hub}
 * <p>
 * Default hub proxy is implemented by {@link RxJava1ProxyType#SerializedBehaviorRelayProxy}
 */
public class DefaultRxJava1Hub extends AbstractRxJava1Hub {

    @Override
    public RxJava1ProxyType getProxyType(Object tag) {
        return RxJava1ProxyType.SerializedBehaviorRelayProxy;
    }

    @Override
    public boolean canTriggerEmit(Object tag) {
        return true;
    }
}
