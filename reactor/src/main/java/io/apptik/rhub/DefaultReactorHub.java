package io.apptik.rhub;


/**
 * Default Reactor-Core implementation of {@link RSHub}
 * <p>
 * Default hub proxy is implemented by {@link ReactorProxyType#SafeBehaviorProcessorProxy}
 */
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
