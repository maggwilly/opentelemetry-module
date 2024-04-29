package org.mule.extension.opentelemetry.module.internal.config;

import org.mule.extension.opentelemetry.module.internal.TraceContextPropagator;
import org.mule.runtime.extension.api.annotation.param.Parameter;

public class TracingConfig {
    @Parameter
    private TraceContextPropagator contextPropagator;

    public TraceContextPropagator getContextPropagator() {
        return contextPropagator;
    }

    public TracingConfig setContextPropagator(TraceContextPropagator contextPropagator) {
        this.contextPropagator = contextPropagator;
        return this;
    }
}
