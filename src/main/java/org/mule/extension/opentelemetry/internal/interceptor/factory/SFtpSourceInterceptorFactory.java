package org.mule.extension.opentelemetry.internal.interceptor.factory;

import org.mule.extension.opentelemetry.internal.context.ContextManager;
import org.mule.extension.opentelemetry.internal.interceptor.SftpSourceInterceptor;
import org.mule.extension.opentelemetry.internal.service.OpenTelemetryConnectionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SFtpSourceInterceptorFactory extends AbstractSourceInterceptorFactory{
  private static final Logger LOGGER = LoggerFactory.getLogger(SFtpSourceInterceptorFactory.class);
  public static final String[] OPL_COMPONENTS = new String[] {"sftp:listener"};

  public SFtpSourceInterceptorFactory(OpenTelemetryConnectionHolder connectionHolder,ContextManager contextManager) {
    super(new SftpSourceInterceptor(connectionHolder, contextManager));
  }


  @Override
  protected String[] getComponents() {
    return OPL_COMPONENTS;
  }
}
