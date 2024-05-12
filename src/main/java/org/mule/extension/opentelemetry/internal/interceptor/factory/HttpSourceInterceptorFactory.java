package org.mule.extension.opentelemetry.internal.interceptor.factory;

import org.mule.extension.opentelemetry.internal.context.ContextManager;
import org.mule.extension.opentelemetry.internal.interceptor.HttpSourceInterceptor;
import org.mule.extension.opentelemetry.internal.service.OpenTelemetryConnectionHolder;

import javax.inject.Inject;

public class HttpSourceInterceptorFactory extends AbstractSourceInterceptorFactory{
  public static final String[] OPL_COMPONENTS = new String[] {"http:listener"};

  @Inject
  public HttpSourceInterceptorFactory(OpenTelemetryConnectionHolder connectionHolder, ContextManager contextManager) {
    super(new HttpSourceInterceptor(connectionHolder, contextManager));
  }

  protected   String[] getComponents() {
    return OPL_COMPONENTS;
  }

}
