package org.mule.extension.opentelemetry.internal.interceptor;

import org.mule.extension.opentelemetry.internal.provider.TracingManager;
import org.mule.extension.opentelemetry.internal.singleton.ContextService;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.interception.SourceInterceptorFactory;

import java.util.Arrays;

public class DefaultSourceInterceptorFactory implements SourceInterceptorFactory {
  public static final String[] OPL_COMPONENTS = new String[] {"http:listener"};
  private final DefaultSourceInterceptor sourceInterceptor;

  public DefaultSourceInterceptorFactory(TracingManager tracingManager, ContextService contextService) {
    sourceInterceptor = new DefaultSourceInterceptor(tracingManager, contextService);
  }

  @Override
  public DefaultSourceInterceptor get() {
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
