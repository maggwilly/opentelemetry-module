package org.mule.extension.opentelemetry.internal.interceptor;

import io.opentelemetry.context.Context;
import org.mule.extension.opentelemetry.internal.OpenTelemetryConnection;
import org.mule.extension.opentelemetry.internal.context.ContextManager;
import org.mule.extension.opentelemetry.internal.service.ConnectionHolder;
import org.mule.runtime.api.interception.InterceptionEvent;
import org.mule.runtime.api.interception.ProcessorParameterValue;
import org.mule.runtime.api.store.ObjectStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

public class SFtpContextPropagateProcessorInterceptor extends ContextVarsPropagateProcessorInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SFtpContextPropagateProcessorInterceptor.class);

    public SFtpContextPropagateProcessorInterceptor(ConnectionHolder<OpenTelemetryConnection> connectionHolder, ContextManager contextManager) {
        super(connectionHolder, contextManager);
    }

    protected void doPropagate(InterceptionEvent event, Context currentContext, Map<String, ProcessorParameterValue> parameters) {
        super.doPropagate(event,currentContext,parameters);
        LOGGER.info("Propagating context  {}", parameters);
        OpenTelemetryConnection openTelemetryConnection = connectionHolder.getConnection();
        ObjectStore contextPropagator = openTelemetryConnection.getTracingConfig().getContextPropagator();
        if (Objects.nonNull(contextPropagator)) {
            String contextId = getContextId(parameters);
            contextManager.store(contextPropagator, currentContext, contextId);
            LOGGER.trace("Propagate context in the object store");
        }
    }


    private String getContextId(Map<String, ProcessorParameterValue> parameters) {
        LOGGER.info("Getting context Id {}", parameters);
        ProcessorParameterValue targetPath = parameters.get("targetPath");
        if (Objects.nonNull(targetPath)) {
            return extractValue(targetPath);
        }
        ProcessorParameterValue path = parameters.get("path");
        if (Objects.nonNull(path)) {
            return extractValue(path);
        }
        return "fileName";
    }

    private String extractValue(ProcessorParameterValue targetPath) {
        LOGGER.info("Extracting value {}", targetPath);
        Object resolveValue = targetPath.resolveValue();
        String filePath = Objects.nonNull(resolveValue) ? resolveValue.toString() : targetPath.providedValue();
        if (Objects.nonNull(filePath)) {
            Path path = Paths.get(filePath);
            return path.getFileName().toString();
        }
        return "fileName";
    }
}
