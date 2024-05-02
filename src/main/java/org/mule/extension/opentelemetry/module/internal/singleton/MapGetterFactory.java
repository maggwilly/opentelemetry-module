package org.mule.extension.opentelemetry.module.internal.singleton;

import io.opentelemetry.context.propagation.TextMapGetter;
import org.mule.extension.opentelemetry.module.internal.Factory;
import org.mule.extension.opentelemetry.module.internal.TraceContextPropagator;
import org.mule.extension.opentelemetry.module.trace.DefaultContextMapGetter;
import org.mule.extension.opentelemetry.module.trace.DistributedContextPropagator;
import org.mule.extension.opentelemetry.module.trace.Propagator;

import java.util.Map;

public class MapGetterFactory implements Factory<TraceContextPropagator, TextMapGetter<Map<String, String>>> {
    @Override
    public TextMapGetter<Map<String, String>> create(TraceContextPropagator contextPropagator) {
        if(contextPropagator instanceof DistributedContextPropagator){
            Propagator propagator = ((DistributedContextPropagator) contextPropagator).getPropagator();
           return propagator.getter();
        }
        return DefaultContextMapGetter.INSTANCE;
    }
}
