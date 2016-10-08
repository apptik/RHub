package io.apptik.rhub;


public class DefaultReactorHub extends AbstractReactorHub {

    @Override
    public ProxyType getProxyType(Object tag) {
        return ReactorProxyType.SafeBehaviorProcessorProxy;
    }

    @Override
    public boolean canTriggerEmit(Object tag) {
        return true;
    }
}
