package org.mule.extension.opentelemetry.internal.interceptor;

import io.opentelemetry.context.Context;
import org.mule.extension.opentelemetry.internal.OpenTelemetryConnection;
import org.mule.extension.opentelemetry.internal.service.ConnectionHolder;
import org.mule.extension.opentelemetry.internal.service.ContextPropagator;

import org.mule.extension.opentelemetry.internal.service.OpenTelemetryConnectionHolder;
import org.mule.extension.opentelemetry.trace.ContextMapSetter;
import org.mule.extension.opentelemetry.util.OplConstants;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.interception.InterceptionEvent;
import org.mule.runtime.api.interception.ProcessorInterceptor;
import org.mule.runtime.api.interception.ProcessorParameterValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DefaultProcessorInterceptor implements ProcessorInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProcessorInterceptor.class);
    private final ConnectionHolder<OpenTelemetryConnection> connectionHolder;
    public DefaultProcessorInterceptor(ConnectionHolder<OpenTelemetryConnection> connectionHolder) {
        this.connectionHolder = connectionHolder;
    }

    public void after(ComponentLocation location, InterceptionEvent event, Optional<Throwable> thrown) {
        OpenTelemetryConnection openTelemetryConnection = connectionHolder.getConnection();
        ComponentIdentifier identifier = location.getComponentIdentifier().getIdentifier();
        LOGGER.trace("After Interception - {}, identifier {}, namespace={}", location, identifier.getName(), identifier.getNamespace());
        ContextPropagator contextPropagator = openTelemetryConnection.getContextPropagator();
        Context currentContext = contextPropagator.retrieveLocally(event.getContext().getId());
        Map<String,String> carrier = new HashMap<>();
        contextPropagator.injectTraceContext(currentContext, carrier, ContextMapSetter.INSTANCE);
        event.addVariable(OplConstants.TRACE_CONTEXT_MAP_KEY,carrier);
    }

    @Override
    public void before(ComponentLocation location, Map<String, ProcessorParameterValue> parameters, InterceptionEvent event) {
        ComponentIdentifier identifier = location.getComponentIdentifier().getIdentifier();
        LOGGER.trace("before Interception - {}, identifier {}, namespace={}", location, identifier.getName(), identifier.getNamespace());
    }
}
