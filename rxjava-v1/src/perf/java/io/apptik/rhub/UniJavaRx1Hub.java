package io.apptik.rhub;


public class UniJavaRx1Hub extends AbstractRxJava1Hub {

    public final RxJava1ProxyType proxyType;

    public UniJavaRx1Hub(RxJava1ProxyType proxyType) {
        this.proxyType = proxyType;
    }

    @Override
    public RxJava1ProxyType getProxyType(Object tag) {
        return proxyType;
    }

    @Override
    public boolean canTriggerEmit(Object tag) {
        return true;
    }
}
