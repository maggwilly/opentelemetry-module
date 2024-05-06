package org.mule.extension.opentelemetry.internal.singleton;

import io.opentelemetry.context.propagation.TextMapGetter;
import org.mule.extension.opentelemetry.internal.Factory;
import org.mule.extension.opentelemetry.internal.TraceContextPropagator;
import org.mule.extension.opentelemetry.trace.DistributedContextPropagator;
import org.mule.extension.opentelemetry.trace.Propagator;

import java.util.Map;

public class MapGetterFactory implements Factory<TraceContextPropagator, TextMapGetter<Map<String, String>>> {
    @Override
    public TextMapGetter<Map<String, String>> create(TraceContextPropagator contextPropagator) {
        if(contextPropagator instanceof DistributedContextPropagator){
            String contextId = ((DistributedContextPropagator) contextPropagator).getContextId();
            Propagator propagator =  ((DistributedContextPropagator) contextPropagator).getPropagator();
           return propagator.getter(contextId);
        }
        return null;
    }
}
