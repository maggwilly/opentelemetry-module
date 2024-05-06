package org.mule.extension.opentelemetry.internal.notification;

import org.mule.extension.opentelemetry.internal.singleton.ContextService;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.interception.ProcessorInterceptor;
import org.mule.runtime.api.interception.ProcessorInterceptorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;

public class MessageProcessorTracingInterceptorFactory implements ProcessorInterceptorFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessorTracingInterceptorFactory.class);

    public static final String[] OPL_COMPONENTS = new String[] {"opl:create-span","opl:create-span"};
    private final ProcessorTracingInterceptor processorTracingInterceptor;
    @Inject
    public MessageProcessorTracingInterceptorFactory(ContextService contextService) {
        this.processorTracingInterceptor = new ProcessorTracingInterceptor(contextService);
    }

    @Override
    public boolean intercept(ComponentLocation location) {
        ComponentIdentifier identifier = location.getComponentIdentifier().getIdentifier();
        LOGGER.info("intercept  - {}, identifier {}, namespace={}", location, identifier, identifier.getNamespace());
        return Arrays.stream(OPL_COMPONENTS).anyMatch(s -> s.equals(getName(identifier)));
    }

    private String getName(ComponentIdentifier identifier) {
        return String.format("%s:%s",identifier.getNamespace(),identifier.getName());
    }

    @Override
    public ProcessorInterceptor get() {
        return processorTracingInterceptor;
    }
}
