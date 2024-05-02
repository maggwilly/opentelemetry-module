package org.mule.extension.opentelemetry.module.trace;

import io.opentelemetry.context.propagation.TextMapPropagator;
import org.mule.extension.opentelemetry.module.internal.TraceContextPropagator;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.util.HashMap;
import java.util.Map;

public class DistributedContextPropagator implements TraceContextPropagator {
    public static final String CONTEXT_ID_KEY = "contextId";

    @Parameter
    private String contextId;

    @Parameter
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    private Propagator propagator;

    @Content
    @Parameter
    @Optional
    @NullSafe(defaultImplementingType = HashMap.class)
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    private Map<String, String> attributes;

    @Override
    public Map<String, String> getAttributes() {
        attributes.put(CONTEXT_ID_KEY, contextId);
        return attributes;
    }

    @Override
    public TextMapPropagator getTxtMapPropagator() {
        return JsonTraceContextPropagator.getInstance();
    }

    public String getContextId() {
        return contextId;
    }

    public DistributedContextPropagator setContextId(String contextId) {
        this.contextId = contextId;
        attributes.put(CONTEXT_ID_KEY,contextId);
        return this;
    }

    public Propagator getPropagator() {
        return propagator;
    }

    public DistributedContextPropagator setPropagator(Propagator propagator) {
        this.propagator = propagator;
        return this;
    }

}
