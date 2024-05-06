package org.mule.extension.opentelemetry.trace;

import org.mule.extension.opentelemetry.internal.TraceContextPropagator;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;

public class DistributedContextPropagator implements TraceContextPropagator {
    @Parameter
    private String contextId;

    @Parameter
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    private Propagator propagator;

    public String getContextId() {
        return contextId;
    }

    public DistributedContextPropagator setContextId(String contextId) {
        this.contextId = contextId;
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
