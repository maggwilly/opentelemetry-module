package org.mule.extension.opentelemetry.module.internal;

import io.opentelemetry.context.propagation.TextMapPropagator;
import org.mule.extension.opentelemetry.module.trace.JsonTraceContextPropagator;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.util.HashMap;
import java.util.Map;

public class DistributedHttpTraceContextPropagator implements TraceContextPropagator {
    @Parameter
    private String contextId;

    @Override
    public Map<String, String> getAttributes() {
        return new HashMap<String, String>() {{
            put("contextId", contextId);
        }};
    }

    @Override
    public TextMapPropagator getTxtMapPropagator() {
        return JsonTraceContextPropagator.getInstance();
    }

    public DistributedHttpTraceContextPropagator setContextId(String contextId) {
        this.contextId = contextId;
        return this;
    }

    @Override
    public PropagatorType getType() {
        return PropagatorType.JSON_DISTRIBUTED;
    }
}
