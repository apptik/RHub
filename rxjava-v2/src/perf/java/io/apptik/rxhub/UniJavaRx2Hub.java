package io.apptik.rxhub;


public class UniJavaRx2Hub extends AbstractRxJava2Hub {

    public final ProxyType proxyType;

    public UniJavaRx2Hub(ProxyType proxyType) {
        this.proxyType = proxyType;
    }

    @Override
    public ProxyType getProxyType(Object tag) {
        return proxyType;
    }

    @Override
    public boolean isProxyThreadsafe(Object tag) {
        return false;
    }

    @Override
    public boolean canTriggerEmit(Object tag) {
        return true;
    }
}
