package io.apptik.rhub;


import static io.apptik.rhub.RxJava2SubjProxyType.SafeBehaviorSubjectProxy;

public class DefaultRxJava2ObsHub extends AbstractRxJava2ObsHub {

    @Override
    public ProxyType getProxyType(Object tag) {
        return SafeBehaviorSubjectProxy;
    }

    @Override
    public boolean canTriggerEmit(Object tag) {
        return true;
    }
}
