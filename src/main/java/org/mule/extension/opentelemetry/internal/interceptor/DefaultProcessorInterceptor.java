package org.mule.extension.opentelemetry.internal.interceptor;

import io.opentelemetry.context.Context;
import org.mule.extension.opentelemetry.internal.singleton.ContextService;

import org.mule.extension.opentelemetry.trace.ContextMapSetter;
import org.mule.extension.opentelemetry.util.OplConstants;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.interception.InterceptionEvent;
import org.mule.runtime.api.interception.ProcessorInterceptor;
import org.mule.runtime.api.interception.ProcessorParameterValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DefaultProcessorInterceptor implements ProcessorInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProcessorInterceptor.class);
    private final ContextService contextService;

    public DefaultProcessorInterceptor(ContextService contextService) {
        this.contextService = contextService;
    }

    public void after(ComponentLocation location, InterceptionEvent event, Optional<Throwable> thrown) {
        ComponentIdentifier identifier = location.getComponentIdentifier().getIdentifier();
        LOGGER.trace("After Interception - {}, identifier {}, namespace={}", location, identifier.getName(), identifier.getNamespace());
        Context currentContext = contextService.retrieveLocally(event.getContext().getId());
        Map<String,String> carrier = new HashMap<>();
        contextService.injectTraceContext(currentContext, carrier, ContextMapSetter.INSTANCE);
        event.addVariable(OplConstants.TRACE_CONTEXT_MAP_KEY,carrier);
    }

    @Override
    public void before(ComponentLocation location, Map<String, ProcessorParameterValue> parameters, InterceptionEvent event) {
        ComponentIdentifier identifier = location.getComponentIdentifier().getIdentifier();
        LOGGER.trace("before Interception - {}, identifier {}, namespace={}", location, identifier.getName(), identifier.getNamespace());
    }
}
