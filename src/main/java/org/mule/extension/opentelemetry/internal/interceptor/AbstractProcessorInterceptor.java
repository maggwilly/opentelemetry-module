package org.mule.extension.opentelemetry.internal.interceptor;

import org.mule.extension.opentelemetry.internal.OpenTelemetryConnection;
import org.mule.extension.opentelemetry.internal.context.ContextManager;
import org.mule.extension.opentelemetry.internal.service.ConnectionHolder;
import org.mule.extension.opentelemetry.util.OplUtils;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.interception.InterceptionEvent;
import org.mule.runtime.api.interception.ProcessorInterceptor;
import org.mule.runtime.api.interception.ProcessorParameterValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;


public abstract class AbstractProcessorInterceptor implements ProcessorInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContextVarsPropagateProcessorInterceptor.class);

    protected final ConnectionHolder<OpenTelemetryConnection> connectionHolder;
    protected final ContextManager contextManager;

    public AbstractProcessorInterceptor(ConnectionHolder<OpenTelemetryConnection> connectionHolder, ContextManager contextManager) {
        this.connectionHolder = connectionHolder;
        this.contextManager = contextManager;
    }

    @Override
    public void before(ComponentLocation location, Map<String, ProcessorParameterValue> parameters, InterceptionEvent event) {
        String transactionId = OplUtils.createTransactionId(event.getContext().getId(), location);
        LOGGER.info("Before processor {} - {}/ transactionId {}", location, parameters, transactionId);

    }

    @Override
    public void after(ComponentLocation location, InterceptionEvent event, Optional<Throwable> thrown) {

    }
}
