package org.mule.extension.opentelemetry.internal.interceptor;

import org.mule.extension.opentelemetry.internal.OpenTelemetryConnection;
import org.mule.extension.opentelemetry.internal.exception.SpanException;
import org.mule.extension.opentelemetry.internal.service.ConnectionHolder;
import org.mule.extension.opentelemetry.internal.service.TraceCollector;
import org.mule.extension.opentelemetry.trace.SpanWrapper;
import org.mule.extension.opentelemetry.trace.Transaction;
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

    protected Transaction startTransaction(ComponentLocation componentLocation, Event event) throws SpanException {
        LOGGER.trace("Starting transaction {}", componentLocation);
        OpenTelemetryConnection holderConnection = connectionHolder.getConnection();
        SpanWrapper spanWrapper = this.createSpan(event, componentLocation);
        TraceCollector traceCollector = holderConnection.getTraceCollector();
        return traceCollector.startTransaction(spanWrapper);
    }

    protected abstract SpanWrapper createSpan(Event event, ComponentLocation componentLocation) throws SpanException;
}
