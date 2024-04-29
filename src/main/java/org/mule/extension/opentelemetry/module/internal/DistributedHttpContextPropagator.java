package org.mule.extension.opentelemetry.module.internal;

import io.opentelemetry.context.propagation.TextMapPropagator;
import org.mule.extension.http.internal.request.HttpRequesterProvider;
import org.mule.extension.opentelemetry.module.trace.JsonTraceContextPropagator;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.util.HashMap;
import java.util.Map;

public class DistributedHttpContextPropagator extends HttpRequesterProvider implements TraceContextPropagator {
    public static final String CONTEXT_ID = "contextId";
    @Parameter
    private String contextId;

    @Override
    public Map<String, String> getAttributes() {
        return new HashMap<String, String>() {{
            put(CONTEXT_ID, contextId);
        }};
    }

    @Override
    public TextMapPropagator getTxtMapPropagator() {
        return JsonTraceContextPropagator.getInstance();
    }

    public DistributedHttpContextPropagator setContextId(String contextId) {
        this.contextId = contextId;
        return this;
    }

    public String getContextId() {
        return contextId;
    }

    @Override
    public PropagatorType getType() {
        return PropagatorType.JSON_DISTRIBUTED;
    }
}
