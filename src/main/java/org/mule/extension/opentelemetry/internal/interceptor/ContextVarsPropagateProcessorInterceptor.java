package org.mule.extension.opentelemetry.internal.interceptor;

import io.opentelemetry.context.Context;
import org.mule.extension.opentelemetry.internal.OpenTelemetryConnection;
import org.mule.extension.opentelemetry.internal.context.ContextManager;
import org.mule.extension.opentelemetry.internal.service.ConnectionHolder;
import org.mule.extension.opentelemetry.trace.ContextMapSetter;
import org.mule.extension.opentelemetry.util.OplConstants;
import org.mule.runtime.api.interception.InterceptionEvent;
import org.mule.runtime.api.interception.ProcessorParameterValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ContextVarsPropagateProcessorInterceptor extends AbstractContextPropagateProcessorInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContextVarsPropagateProcessorInterceptor.class);

    public ContextVarsPropagateProcessorInterceptor(ConnectionHolder<OpenTelemetryConnection> connectionHolder, ContextManager contextManager) {
        super(connectionHolder, contextManager);
    }

    protected void doPropagate(InterceptionEvent event, Context currentContext, Map<String, ProcessorParameterValue> parameters) {
        LOGGER.info("Propagating context  {}", parameters);
        Map<String, String> carrier = new HashMap<>();
        contextManager.injectTraceContext(currentContext, carrier, ContextMapSetter.INSTANCE);
        event.addVariable(OplConstants.TRACE_CONTEXT_MAP_KEY, carrier);
        LOGGER.trace("Propagate context in vars: {}", carrier);
    }
}
