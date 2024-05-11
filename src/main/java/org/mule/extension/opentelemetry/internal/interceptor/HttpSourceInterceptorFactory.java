package org.mule.extension.opentelemetry.internal.interceptor;

import org.mule.extension.opentelemetry.internal.context.ContextManager;
import org.mule.extension.opentelemetry.internal.service.OpenTelemetryConnectionHolder;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.interception.SourceInterceptorFactory;

import javax.inject.Inject;
import java.util.Arrays;

public class HttpSourceInterceptorFactory implements SourceInterceptorFactory{
  public static final String[] OPL_COMPONENTS = new String[] {"http:listener"};
  private final HttpSourceInterceptor sourceInterceptor;

  @Inject
  public HttpSourceInterceptorFactory(OpenTelemetryConnectionHolder connectionHolder, ContextManager contextManager) {
    sourceInterceptor = new HttpSourceInterceptor(connectionHolder, contextManager);
  }

  @Override
  public HttpSourceInterceptor get() {
    return sourceInterceptor;
  }

  @Override
  public boolean intercept(ComponentLocation location) {
    ComponentIdentifier identifier = location.getComponentIdentifier().getIdentifier();
    return Arrays.stream(OPL_COMPONENTS).anyMatch(s -> s.equals(getName(identifier)));
  }

  private String getName(ComponentIdentifier identifier) {
    return String.format("%s:%s",identifier.getNamespace(),identifier.getName());
  }

}
