package org.mule.extension.opentelemetry.internal.interceptor;

import io.opentelemetry.context.Context;
import org.mule.extension.opentelemetry.internal.OpenTelemetryConnection;
import org.mule.extension.opentelemetry.internal.service.ConnectionHolder;
import org.mule.extension.opentelemetry.internal.context.ContextManager;
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

public class DefaultProcessorInterceptor implements ProcessorInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProcessorInterceptor.class);
    private final ConnectionHolder<OpenTelemetryConnection> connectionHolder;
    private final ContextManager contextManager;
    public DefaultProcessorInterceptor(ConnectionHolder<OpenTelemetryConnection> connectionHolder,ContextManager contextManager) {
        this.connectionHolder = connectionHolder;
        this.contextManager = contextManager;
    }

    @Override
    public void before(ComponentLocation location, Map<String, ProcessorParameterValue> parameters, InterceptionEvent event) {
        try {
          this.handler(location, event);
        } catch (Exception e) {
            LOGGER.error("Before Interception", e);
        }
    }

    private void handler(ComponentLocation location, InterceptionEvent event) {
        OpenTelemetryConnection openTelemetryConnection = connectionHolder.getConnection();
        ComponentIdentifier identifier = location.getComponentIdentifier().getIdentifier();
        LOGGER.trace("Before Interception - {}, identifier {}, namespace={}", location, identifier.getName(), identifier.getNamespace());
        Context currentContext = contextManager.retrieve(event.getContext().getId());
        Map<String, String> carrier = new HashMap<>();
        contextManager.injectTraceContext(currentContext, carrier, ContextMapSetter.INSTANCE);
        event.addVariable(OplConstants.TRACE_CONTEXT_MAP_KEY, carrier);
    }
}
