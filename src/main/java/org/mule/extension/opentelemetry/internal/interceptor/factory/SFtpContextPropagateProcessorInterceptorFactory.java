package org.mule.extension.opentelemetry.internal.interceptor.factory;

import org.mule.extension.opentelemetry.internal.context.ContextManager;
import org.mule.extension.opentelemetry.internal.interceptor.SFtpContextPropagateProcessorInterceptor;
import org.mule.extension.opentelemetry.internal.service.OpenTelemetryConnectionHolder;

import javax.inject.Inject;

public class SFtpContextPropagateProcessorInterceptorFactory extends AbstractProcessorInterceptorFactory {
    public static final String[] OPL_COMPONENTS = new String[]{"sftp:write","sftp:copy" ,"sftp:move","sftp:rename"};
    @Inject
    public SFtpContextPropagateProcessorInterceptorFactory(OpenTelemetryConnectionHolder connectionHolder, ContextManager contextManager) {
        super(new SFtpContextPropagateProcessorInterceptor(connectionHolder, contextManager));
    }

    protected   String[] getComponents() {
        return OPL_COMPONENTS;
    }

}
