package org.mule.extension.opentelemetry.module.internal.singleton;

import io.opentelemetry.context.propagation.TextMapSetter;
import org.mule.extension.opentelemetry.module.internal.Factory;
import org.mule.extension.opentelemetry.module.internal.TraceContextPropagator;
import org.mule.extension.opentelemetry.module.trace.ContextMapSetter;
import org.mule.extension.opentelemetry.module.trace.DistributedContextPropagator;
import org.mule.extension.opentelemetry.module.trace.Propagator;

import java.util.Map;

public class MapSetterFactory implements Factory<TraceContextPropagator, TextMapSetter<Map<String, String>>> {
    @Override
    public TextMapSetter<Map<String, String>> create(TraceContextPropagator contextPropagator) {
        if(contextPropagator instanceof DistributedContextPropagator){
            String contextId = ((DistributedContextPropagator) contextPropagator).getContextId();
            Propagator propagator =  ((DistributedContextPropagator) contextPropagator).getPropagator();
            return propagator.setter(contextId);
        }
        return ContextMapSetter.INSTANCE;
    }
}
