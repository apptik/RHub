package io.apptik.rhub;


public class UniJavaRx2PubHub extends AbstractRxJava2PubHub {

    public final ProxyType proxyType;

    public UniJavaRx2PubHub(ProxyType proxyType) {
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
