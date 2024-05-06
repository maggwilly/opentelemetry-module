package org.mule.extension.opentelemetry.internal.interceptor;

import org.mule.extension.http.api.HttpRequestAttributes;
import org.mule.extension.opentelemetry.api.SpanContextHolder;
import org.mule.extension.opentelemetry.api.TextMapContextHolder;
import org.mule.extension.opentelemetry.internal.provider.TracingManager;
import org.mule.extension.opentelemetry.internal.singleton.ContextService;
import org.mule.extension.opentelemetry.trace.FlowSpan;
import org.mule.extension.opentelemetry.trace.SpanWrapper;
import org.mule.extension.opentelemetry.util.OplUtils;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.interception.InterceptionEvent;
import org.mule.runtime.api.interception.ProcessorParameterValue;
import org.mule.runtime.api.interception.SourceInterceptor;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.util.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

import static io.opentelemetry.semconv.SemanticAttributes.*;

public class DefaultSourceInterceptor implements SourceInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSourceInterceptor.class);
    private final TracingManager tracingManager;
    private final ContextService contextService;

  public DefaultSourceInterceptor(TracingManager tracingManager, ContextService contextService) {
        this.tracingManager = tracingManager;
        this.contextService = contextService;
  }

    @Override
    public void beforeCallback(ComponentLocation location, Map<String, ProcessorParameterValue> parameters, InterceptionEvent event) {
        LOGGER.trace("######## Before callback: {}", location);
    }

    @Override
    public void afterCallback(ComponentLocation componentLocation, InterceptionEvent event, Optional<Throwable> thrown) {
        LOGGER.trace("######## After callback: {}", componentLocation);

        TypedValue<Object> attributes = event.getMessage().getAttributes();
        HttpRequestAttributes attr = (HttpRequestAttributes) attributes.getValue();
        MultiMap<String, String> headers = attr.getHeaders();
        SpanContextHolder textMapContextHolder = new TextMapContextHolder().setValue(headers);

        String contextId = event.getContext().getId();
        MultiMap<String, String> stringStringMap = createAttributes(attr);
        FlowSpan span = new FlowSpan().setName(componentLocation.getRootContainerName()).setAttributes(stringStringMap).setContextId(contextId);
        SpanWrapper trace = OplUtils.createSpan(span, contextId, componentLocation);

        tracingManager.startTransaction(textMapContextHolder, trace);
    }


    private MultiMap<String, String> createAttributes(HttpRequestAttributes attributes) {
        MultiMap<String, String> tags = new MultiMap<>();
        tags.put(SERVER_ADDRESS.getKey(), attributes.getHeaders().get("host"));
        tags.put(MESSAGING_CLIENT_ID.getKey(), attributes.getHeaders().get("client_id"));
        tags.put(HTTP_REQUEST_METHOD.getKey(), attributes.getMethod());
        tags.put(URL_SCHEME.getKey(), attributes.getScheme());
        tags.put(HTTP_ROUTE.getKey(), attributes.getListenerPath());
        tags.put(URL_PATH.getKey(), attributes.getRequestPath());
        return tags;
    }

}
