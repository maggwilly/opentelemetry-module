package org.mule.extension.opentelemetry.internal.notification;

import io.opentelemetry.context.Context;
import org.mule.extension.opentelemetry.internal.singleton.ContextService;

import org.mule.extension.opentelemetry.trace.ContextMapSetter;
import org.mule.extension.opentelemetry.util.OplConstants;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.interception.InterceptionEvent;
import org.mule.runtime.api.interception.ProcessorInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProcessorTracingInterceptor  implements ProcessorInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorTracingInterceptor.class);

    private final ContextService contextService;

    public ProcessorTracingInterceptor(ContextService contextService) {
        this.contextService = contextService;
    }

    public void after(ComponentLocation location, InterceptionEvent event, Optional<Throwable> thrown) {
        ComponentIdentifier identifier = location.getComponentIdentifier().getIdentifier();
        LOGGER.info("After Interception - {}, identifier {}, namespace={}", location, identifier.getName(), identifier.getNamespace());
        Context currentContext = contextService.retrieveLocally(event.getContext().getId());
        Map<String,String> carrier = new HashMap<>();
        contextService.injectTraceContext(currentContext, carrier, ContextMapSetter.INSTANCE);
        event.addVariable(OplConstants.TRACE_CONTEXT_MAP_KEY,carrier);
    }
}
