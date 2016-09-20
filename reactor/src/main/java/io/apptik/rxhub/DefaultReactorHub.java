package io.apptik.rxhub;


public class DefaultReactorHub extends AbstractReactorHub {

    @Override
    public ProxyType getProxyType(Object tag) {
        return ReactorProxyType.BehaviorSafeProxy;
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
