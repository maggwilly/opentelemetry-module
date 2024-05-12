package org.mule.extension.opentelemetry.internal.interceptor;

import io.opentelemetry.context.Context;
import org.mule.extension.opentelemetry.internal.OpenTelemetryConnection;
import org.mule.extension.opentelemetry.internal.context.ContextManager;
import org.mule.extension.opentelemetry.internal.service.ConnectionHolder;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.interception.InterceptionEvent;
import org.mule.runtime.api.interception.ProcessorParameterValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract  class AbstractContextPropagateProcessorInterceptor extends AbstractProcessorInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContextVarsPropagateProcessorInterceptor.class);

    public AbstractContextPropagateProcessorInterceptor(ConnectionHolder<OpenTelemetryConnection> connectionHolder, ContextManager contextManager) {
        super(connectionHolder, contextManager);
    }


    @Override
    public void before(ComponentLocation location, Map<String, ProcessorParameterValue> parameters, InterceptionEvent event) {
        super.before(location, parameters , event);
        ComponentIdentifier identifier = location.getComponentIdentifier().getIdentifier();
        try {
          LOGGER.trace("Before Interception - {}, identifier {}, namespace={}", location, identifier.getName(), identifier.getNamespace());
          Context currentContext = contextManager.retrieve(event.getContext().getId());
          this.doPropagate(event, currentContext, parameters);
      }catch (Exception e){
          LOGGER.error("Failed to propagate context - {}, identifier {}, namespace={}", location, identifier.getName(), identifier.getNamespace());
      }
    }

    protected abstract void doPropagate(InterceptionEvent event, Context currentContext, Map<String, ProcessorParameterValue> parameters);

}
