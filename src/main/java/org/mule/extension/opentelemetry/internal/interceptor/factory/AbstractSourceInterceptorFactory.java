package org.mule.extension.opentelemetry.internal.interceptor.factory;

import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.interception.SourceInterceptor;
import org.mule.runtime.api.interception.SourceInterceptorFactory;

import java.util.Arrays;

public abstract class AbstractSourceInterceptorFactory implements SourceInterceptorFactory {
    private final SourceInterceptor sourceInterceptor;

    public AbstractSourceInterceptorFactory(SourceInterceptor sourceInterceptor) {
        this.sourceInterceptor = sourceInterceptor;
    }

    @Override
    public SourceInterceptor get() {
        return sourceInterceptor;
    }
    @Override
    public boolean intercept(ComponentLocation location) {
        ComponentIdentifier identifier = location.getComponentIdentifier().getIdentifier();
        return Arrays.stream(getComponents()).anyMatch(s -> s.equals(getName(identifier)));
    }

    private String getName(ComponentIdentifier identifier) {
        return String.format("%s:%s",identifier.getNamespace(),identifier.getName());
    }

    protected abstract   String[] getComponents();
}
