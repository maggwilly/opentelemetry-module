package org.mule.extension.opentelemetry.module.internal.singleton;

import io.opentelemetry.context.Context;
import org.mule.extension.opentelemetry.module.api.SpanContextHolder;

public interface ContextService {
    Context extractContext(SpanContextHolder source);

    void storeLocal(Context extractContext, String transactionId);

    Context retrieveLocal(String transactionId);
}
