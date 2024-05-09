package org.mule.extension.opentelemetry.internal.interceptor;

import org.mule.extension.opentelemetry.internal.OpenTelemetryConnection;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.interception.SourceInterceptorFactory;
import org.mule.runtime.extension.api.annotation.param.Connection;

import javax.inject.Inject;
import java.util.Arrays;

public class DefaultSourceInterceptorFactory implements SourceInterceptorFactory {
  public static final String[] OPL_COMPONENTS = new String[] {"http:listener"};
  private final DefaultSourceInterceptor sourceInterceptor;
  @Inject
  public DefaultSourceInterceptorFactory(@Connection OpenTelemetryConnection openTelemetryConnection) {
    sourceInterceptor = new DefaultSourceInterceptor(openTelemetryConnection.getTraceCollector());
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
