package org.mule.extension.opentelemetry.module.trace;

import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.mule.extension.opentelemetry.module.internal.TraceContextPropagator;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.util.HashMap;
import java.util.Map;

public class LocalContextPropagator implements TraceContextPropagator {
    @Content
    @Parameter
    @Optional
    @NullSafe(defaultImplementingType = HashMap.class)
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    private Map<String, String> attributes;

    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public TextMapPropagator getTxtMapPropagator() {
        return W3CTraceContextPropagator.getInstance();
    }

    @Override
    public TextMapGetter<Map<String, String>> getter() {
        return DefaultContextMapGetter.INSTANCE;
    }

    @Override
    public TextMapSetter<Map<String, String>> Setter() {
        return DefaultContextMapSetter.INSTANCE;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

}
