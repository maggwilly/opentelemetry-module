package org.mule.extension.opentelemetry.internal.context;

import io.opentelemetry.context.Context;
import org.mule.extension.http.api.HttpRequestAttributes;
import org.mule.extension.http.api.HttpResponseAttributes;
import org.mule.extension.opentelemetry.internal.exception.ParentContextException;
import org.mule.extension.opentelemetry.trace.ContextMapGetter;
import org.mule.extension.opentelemetry.util.OplUtils;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.api.i18n.I18nMessageFactory;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.util.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.opentelemetry.semconv.SemanticAttributes.*;

public class HttpHeadersSpanCreator extends AbstractSpanCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpHeadersSpanCreator.class);

    private final ContextManager contextManager;

    public HttpHeadersSpanCreator(ContextManager contextManager) {
        this.contextManager = contextManager;
    }

    public void extractParentContext(Event event) throws ParentContextException {
        try {
            LOGGER.trace("Attributes {}", event.getMessage().getAttributes());
            TypedValue<HttpRequestAttributes> attributes = event.getMessage().getAttributes();
            HttpRequestAttributes attributesValue = attributes.getValue();

            Context traceContext = contextManager.getTraceContext(attributesValue.getHeaders(), ContextMapGetter.INSTANCE);
            String parentTransactionId = OplUtils.getParentTransactionId(event.getContext().getId());
            contextManager.store(traceContext, parentTransactionId);
        } catch (Exception e) {
            LOGGER.error("Failed to extract parent context from event {}", e.getMessage());
            throw new ParentContextException(I18nMessageFactory.createStaticMessage("Failed to extract parent context from event"));
        }
    }


    protected MultiMap<String, String> getAttributes(Event event){
        TypedValue<Object> attr = event.getMessage().getAttributes();
        HttpRequestAttributes attributes = (HttpRequestAttributes) attr.getValue();
        MultiMap<String, String> tags = new MultiMap<>();
        tags.put("mule.event.correlationId", event.getCorrelationId());
        tags.put(SERVER_ADDRESS.getKey(), attributes.getHeaders().get("host"));
        tags.put(MESSAGING_CLIENT_ID.getKey(), attributes.getHeaders().get("client_id"));
        tags.put(HTTP_REQUEST_METHOD.getKey(), attributes.getMethod());
        tags.put(URL_SCHEME.getKey(), attributes.getScheme());
        tags.put(HTTP_ROUTE.getKey(), attributes.getListenerPath());
        tags.put(URL_PATH.getKey(), attributes.getRequestPath());
        return tags;
    }
}
