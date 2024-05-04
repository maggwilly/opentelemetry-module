package org.mule.extension.opentelemetry.module.internal.provider;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import org.mule.extension.opentelemetry.module.internal.config.TracingConfig;
import org.mule.extension.opentelemetry.module.api.SpanContextHolder;
import org.mule.extension.opentelemetry.module.trace.SpanWrapper;

public interface TracingManager {
  void openTransaction(SpanWrapper spanWrapper, SpanContextHolder patent, TracingConfig tracingConfig);
  void closeTransaction(SpanWrapper spanWrapper);

  <T> Context getTraceContext(T carrier, TextMapGetter<T> textMapGetter);
}
