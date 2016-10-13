package io.apptik.rhub;


public class UniJavaRx2ObsHub extends AbstractRxJava2ObsHub {

    public final ProxyType proxyType;

    public UniJavaRx2ObsHub(ProxyType proxyType) {
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
