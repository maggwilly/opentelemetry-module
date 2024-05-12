package org.mule.extension.opentelemetry.internal.interceptor;

import org.mule.extension.opentelemetry.internal.OpenTelemetryConnection;
import org.mule.extension.opentelemetry.internal.service.ConnectionHolder;
import org.mule.extension.opentelemetry.trace.Transaction;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.interception.InterceptionEvent;
import org.mule.runtime.api.interception.ProcessorParameterValue;
import org.mule.runtime.api.interception.SourceInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public abstract class AbstractSourceInterceptor extends AbstractTracingHandler implements SourceInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSourceInterceptor.class);

    public AbstractSourceInterceptor(ConnectionHolder<OpenTelemetryConnection> connectionHolder) {
        super(connectionHolder);
    }

    @Override
    public void beforeCallback(ComponentLocation componentLocation, Map<String, ProcessorParameterValue> parameters, InterceptionEvent event) {
        LOGGER.trace("######## Before callback: {}", componentLocation);
        try {
            Transaction transaction = this.startTransaction(componentLocation, event);
            LOGGER.trace("Start Transaction: {}", transaction);
        } catch (Exception e) {
            LOGGER.error("Interception ", e.getCause());
        }
    }

    @Override
    public void afterCallback(ComponentLocation componentLocation, InterceptionEvent event, Optional<Throwable> thrown) {
        LOGGER.trace("######## After callback: {}", componentLocation);
    }
}
