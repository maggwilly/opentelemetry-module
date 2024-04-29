package org.mule.extension.opentelemetry.module.trace;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.api.trace.propagation.internal.W3CTraceContextEncoding;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.*;

@Immutable
public class JsonTraceContextPropagator implements TextMapPropagator {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final List<String> FIELDS = Collections.singletonList("context");
    private static final JsonTraceContextPropagator INSTANCE;

    private JsonTraceContextPropagator() {
    }
    static {
        INSTANCE = new JsonTraceContextPropagator();
    }
    public static JsonTraceContextPropagator getInstance() {
        return INSTANCE;
    }
    @Override
    public Collection<String> fields() {
        return FIELDS;
    }

    @Override
    public <C> void inject(Context context, @Nullable C carrier, TextMapSetter<C> setter) {
        SpanContext spanContext = Span.fromContext(context).getSpanContext();
        if (spanContext.isValid()) {

            Map<String, String> values = new HashMap<>();
            String traceId = spanContext.getTraceId();
            values.put("traceId", traceId);

            String spanId = spanContext.getSpanId();
            values.put("spanId", spanId);

            String traceFlagsHex = spanContext.getTraceFlags().asHex();
            values.put("flags", traceFlagsHex);

            TraceState traceState = spanContext.getTraceState();
            if (!traceState.isEmpty()) {
                values.put("state", W3CTraceContextEncoding.encodeTraceState(traceState));
            }
            String asString = toJson(values);
            setter.set(carrier, "context", asString);
        }
    }

    private String toJson(Map<String, String> values) {
        try {
            return objectMapper.writeValueAsString(values);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    public <C> Context extract(Context context, @Nullable C carrier, TextMapGetter<C> getter) {
        SpanContext spanContext = extractImpl(carrier, getter);
        return !spanContext.isValid() ? context : context.with(Span.wrap(spanContext));
    }

    private <C> SpanContext extractImpl(@Nullable C carrier, TextMapGetter<C> getter) {
        String contextJson = getter.get(carrier, "context");
        if (Strings.isNullOrEmpty(contextJson)) {
            return SpanContext.getInvalid();
        }
        Map<String, String> readValue = getReadValue(contextJson);
        if (Objects.isNull(readValue)) {
            return SpanContext.getInvalid();
        }
        String traceId = readValue.get("traceId");
        String spanId = readValue.get("spanId");
        String flags = readValue.get("flags");

        TraceFlags traceFlags = TraceFlags.fromHex(flags, 0);
        if (Strings.isNullOrEmpty(traceId) || Strings.isNullOrEmpty(spanId) || Strings.isNullOrEmpty(flags)) {
            return SpanContext.getInvalid();
        }
        String traceState = readValue.get("state");
        if (!Strings.isNullOrEmpty(traceState)) {
            TraceState decodeTraceState = W3CTraceContextEncoding.decodeTraceState(traceState);
            return SpanContext.createFromRemoteParent(traceId, spanId, traceFlags, decodeTraceState);
        }
        return SpanContext.createFromRemoteParent(traceId, spanId, traceFlags, TraceState.getDefault());
    }

    private Map<String, String> getReadValue(String contextJson) {
        try {
            return objectMapper.readValue(contextJson, Map.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
