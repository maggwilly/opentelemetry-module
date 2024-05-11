package org.mule.extension.opentelemetry.internal.interceptor;

import org.mule.extension.opentelemetry.internal.context.ContextManager;
import org.mule.extension.opentelemetry.internal.service.OpenTelemetryConnectionHolder;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.interception.ProcessorInterceptor;
import org.mule.runtime.api.interception.ProcessorInterceptorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;

public class DefaultProcessorInterceptorFactory implements ProcessorInterceptorFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProcessorInterceptorFactory.class);
    public static final String[] OPL_COMPONENTS = new String[]{"http:requester"};
    private final DefaultProcessorInterceptor processorInterceptor;

    @Inject
    public DefaultProcessorInterceptorFactory(OpenTelemetryConnectionHolder connectionHolder, ContextManager contextManager) {
        processorInterceptor = new DefaultProcessorInterceptor(connectionHolder, contextManager);
    }

    @Override
    public boolean intercept(ComponentLocation location) {
        ComponentIdentifier identifier = location.getComponentIdentifier().getIdentifier();
        LOGGER.trace("intercept  - {}, identifier {}, namespace={}", location, identifier, identifier.getNamespace());
        return Arrays.stream(OPL_COMPONENTS).anyMatch(s -> s.equals(getName(identifier)));
    }

    private String getName(ComponentIdentifier identifier) {
        return String.format("%s:%s", identifier.getNamespace(), identifier.getName());
    }

    @Override
    public ProcessorInterceptor get() {
        return processorInterceptor;
    }

}
