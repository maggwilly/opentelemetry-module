package org.mule.extension.opentelemetry.internal.interceptor.factory;

import org.mule.extension.opentelemetry.internal.context.ContextManager;
import org.mule.extension.opentelemetry.internal.interceptor.ContextVarsPropagateProcessorInterceptor;
import org.mule.extension.opentelemetry.internal.service.OpenTelemetryConnectionHolder;

import javax.inject.Inject;

public class ContextVarsPropagateProcessorInterceptorFactory extends AbstractProcessorInterceptorFactory {
    public static final String[] OPL_COMPONENTS = new String[]{"http:request", "os:store", "os:retrieve", "os:remove", "ee:transform"};
    @Inject
    public ContextVarsPropagateProcessorInterceptorFactory(OpenTelemetryConnectionHolder connectionHolder, ContextManager contextManager) {
        super(new ContextVarsPropagateProcessorInterceptor(connectionHolder, contextManager));
    }

    protected   String[] getComponents() {
        return OPL_COMPONENTS;
    }

}
