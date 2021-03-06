package io.apptik.rhub;


public class UniReactorHub extends AbstractReactorHub {

    public final ProxyType proxyType;

    public UniReactorHub(ProxyType proxyType) {
        this.proxyType = proxyType;
    }

    @Override
    public ProxyType getProxyType(Object tag) {
        return proxyType;
    }

    @Override
    public boolean canTriggerEmit(Object tag) {
        return true;
    }
}
