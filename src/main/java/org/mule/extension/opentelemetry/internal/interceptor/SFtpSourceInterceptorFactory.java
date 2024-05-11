package org.mule.extension.opentelemetry.internal.interceptor;

import org.mule.extension.opentelemetry.internal.context.ContextManager;
import org.mule.extension.opentelemetry.internal.service.OpenTelemetryConnectionHolder;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.interception.SourceInterceptorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class SFtpSourceInterceptorFactory implements SourceInterceptorFactory{
  private static final Logger LOGGER = LoggerFactory.getLogger(SFtpSourceInterceptorFactory.class);
  public static final String[] OPL_COMPONENTS = new String[] {"sftp:listener"};
  private final SftpSourceInterceptor sourceInterceptor;

  public SFtpSourceInterceptorFactory(OpenTelemetryConnectionHolder connectionHolder,ContextManager contextManager) {
    sourceInterceptor = new SftpSourceInterceptor(connectionHolder, contextManager);
  }

  @Override
  public SftpSourceInterceptor get() {
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
