package org.mule.extension.opentelemetry.internal.interceptor;

import org.mule.extension.opentelemetry.internal.OpenTelemetryConnection;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.interception.ProcessorInterceptor;
import org.mule.runtime.api.interception.ProcessorInterceptorFactory;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;

public class DefaultProcessorInterceptorFactory implements ProcessorInterceptorFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProcessorInterceptorFactory.class);
    public static final String[] OPL_COMPONENTS = new String[] {"opl:create-span","opl:create-span"};
    private final DefaultProcessorInterceptor processorTracingInterceptor;
    @Inject
    public DefaultProcessorInterceptorFactory(@Connection OpenTelemetryConnection openTelemetryConnection) {
        this.processorTracingInterceptor = new DefaultProcessorInterceptor(openTelemetryConnection.getContextPropagator());
    }

    @Override
    public boolean intercept(ComponentLocation location) {
        ComponentIdentifier identifier = location.getComponentIdentifier().getIdentifier();
        LOGGER.trace("intercept  - {}, identifier {}, namespace={}", location, identifier, identifier.getNamespace());
        return Arrays.stream(OPL_COMPONENTS).anyMatch(s -> s.equals(getName(identifier)));
    }

    private String getName(ComponentIdentifier identifier) {
        return String.format("%s:%s",identifier.getNamespace(),identifier.getName());
    }

    @Override
    public ProcessorInterceptor get() {
        return processorTracingInterceptor;
    }
}
