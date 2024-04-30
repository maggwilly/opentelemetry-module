package org.mule.extension.opentelemetry.module.trace;

import org.mule.extension.http.api.request.authentication.HttpRequestAuthentication;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.tls.TlsContextFactory;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

public final class HttpRestPropagator  implements Propagator{
  /**
   * Host where the requests will be sent.
   */
  @Parameter
  @Example("https://www.exemple.com/propagator/v1/context")
  private String url;

  @Parameter
  @Optional
  @Expression(ExpressionSupport.NOT_SUPPORTED)
  @DisplayName("TLS Configuration")
  private TlsContextFactory tlsContext;

  @Parameter
  @Optional
  @Placement( tab = "Authentication")
  @Expression(ExpressionSupport.NOT_SUPPORTED)
  private HttpRequestAuthentication authentication;

  public String getUrl() {
    return url;
  }

  public HttpRestPropagator setUrl(String url) {
    this.url = url;
    return this;
  }

  public TlsContextFactory getTlsContext() {
    return tlsContext;
  }

  public HttpRestPropagator setTlsContext(TlsContextFactory tlsContext) {
    this.tlsContext = tlsContext;
    return this;
  }

  public HttpRequestAuthentication getAuthentication() {
    return authentication;
  }

  public HttpRestPropagator setAuthentication(HttpRequestAuthentication authentication) {
    this.authentication = authentication;
    return this;
  }

  @Override
  public PropagatorType getType() {
    return PropagatorType.HTTP_REST;
  }
}
