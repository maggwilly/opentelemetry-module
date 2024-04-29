package org.mule.extension.opentelemetry.module.internal.provider;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import org.mule.extension.opentelemetry.module.trace.Trace;

public interface TransactionManager {
  void openTransaction(Trace trace);
  void closeTransaction(Trace trace);

  <T> Context getTraceContext(T carrier, TextMapGetter<T> textMapGetter);
}
