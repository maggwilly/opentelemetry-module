package org.mule.extension.opentelemetry.internal.interceptor.factory;

import org.mule.extension.opentelemetry.internal.context.ContextManager;
import org.mule.extension.opentelemetry.internal.interceptor.ContextVarsPropagateProcessorInterceptor;
import org.mule.extension.opentelemetry.internal.service.OpenTelemetryConnectionHolder;

import javax.inject.Inject;

public class SftpProcessorInterceptorFactory extends AbstractProcessorInterceptorFactory {
    public static final String[] OPL_COMPONENTS = new String[]{"http:requester"};
    @Inject
    public SftpProcessorInterceptorFactory(OpenTelemetryConnectionHolder connectionHolder, ContextManager contextManager) {
        super(new ContextVarsPropagateProcessorInterceptor(connectionHolder, contextManager));
    }

    protected  String[] getComponents() {
        return OPL_COMPONENTS;
    }

}
