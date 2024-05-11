package org.mule.extension.opentelemetry.internal.interceptor;

import org.mule.extension.opentelemetry.internal.OpenTelemetryConnection;
import org.mule.extension.opentelemetry.internal.exception.SpanException;
import org.mule.extension.opentelemetry.internal.service.ConnectionHolder;
import org.mule.extension.opentelemetry.trace.SpanWrapper;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTracingHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTracingHandler.class);

    protected final ConnectionHolder<OpenTelemetryConnection> connectionHolder;

    protected AbstractTracingHandler(ConnectionHolder<OpenTelemetryConnection> connectionHolder) {
        this.connectionHolder = connectionHolder;
    }

    protected void handler(ComponentLocation componentLocation, Event event) throws SpanException {
        OpenTelemetryConnection holderConnection = connectionHolder.getConnection();
        LOGGER.info("TracingConfiguration {}", holderConnection);
        SpanWrapper spanWrapper = this.createSpan(event, componentLocation);
        holderConnection.getTraceCollector().startTransaction(spanWrapper);
    }

    protected abstract SpanWrapper createSpan(Event event, ComponentLocation componentLocation) throws SpanException;
}
