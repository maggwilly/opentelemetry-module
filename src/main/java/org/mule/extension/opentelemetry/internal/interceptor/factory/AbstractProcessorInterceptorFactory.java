package org.mule.extension.opentelemetry.internal.interceptor.factory;

import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.interception.ProcessorInterceptor;
import org.mule.runtime.api.interception.ProcessorInterceptorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Predicate;

public abstract class AbstractProcessorInterceptorFactory implements ProcessorInterceptorFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProcessorInterceptorFactory.class);

    private final ProcessorInterceptor processorInterceptor;

    protected AbstractProcessorInterceptorFactory(ProcessorInterceptor processorInterceptor) {
        this.processorInterceptor = processorInterceptor;
    }

    @Override
    public boolean intercept(ComponentLocation location) {
        ComponentIdentifier identifier = location.getComponentIdentifier().getIdentifier();
        LOGGER.trace("intercept  - {}, identifier {}, namespace={}", location, identifier, identifier.getNamespace());
        return Arrays.stream(getComponents()).anyMatch(getMatchingPredicate(identifier));
    }

    protected Predicate<String> getMatchingPredicate(ComponentIdentifier identifier) {
        return s -> s.equals(getName(identifier));
    }

    protected abstract String[] getComponents();


    private String getName(ComponentIdentifier identifier) {
        return String.format("%s:%s", identifier.getNamespace(), identifier.getName());
    }

    @Override
    public ProcessorInterceptor get() {
        return processorInterceptor;
    }
}
