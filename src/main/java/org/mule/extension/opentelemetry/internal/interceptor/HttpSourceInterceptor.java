package org.mule.extension.opentelemetry.internal.interceptor;

import org.mule.extension.opentelemetry.internal.OpenTelemetryConnection;
import org.mule.extension.opentelemetry.internal.context.ContextManager;
import org.mule.extension.opentelemetry.internal.context.HttpHeadersSpanCreator;
import org.mule.extension.opentelemetry.internal.exception.SpanException;
import org.mule.extension.opentelemetry.internal.service.ConnectionHolder;
import org.mule.extension.opentelemetry.trace.SpanWrapper;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.api.i18n.I18nMessageFactory;
import org.mule.runtime.api.interception.InterceptionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpSourceInterceptor extends AbstractSourceInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpSourceInterceptor.class);
    private final ContextManager contextManager;
    public HttpSourceInterceptor(ConnectionHolder<OpenTelemetryConnection> connectionHolder, ContextManager contextManager) {
        super(connectionHolder);
        this.contextManager = contextManager;
    }

    @Override
    protected SpanWrapper createSpan(Event event, ComponentLocation componentLocation) throws SpanException {
        try {
           return new HttpHeadersSpanCreator(contextManager).createSpan(event, componentLocation);
        } catch (Exception e) {
            LOGGER.error("Failed to create span from source {}", e.getMessage());
            throw new SpanException(I18nMessageFactory.createStaticMessage("Failed to create span from source"));
        }
    }

}
