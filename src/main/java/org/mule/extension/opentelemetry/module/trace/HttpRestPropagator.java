package org.mule.extension.opentelemetry.module.trace;

import org.mule.extension.http.api.request.authentication.HttpRequestAuthentication;
import org.mule.extension.opentelemetry.module.internal.http.HttpConnection;
import org.mule.extension.opentelemetry.module.internal.http.HttpRequesterConnectionManager;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Disposable;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.tls.TlsContextFactory;
import org.mule.runtime.api.transformation.TransformationService;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.RefName;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.http.api.client.HttpClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.function.Supplier;

import static org.mule.runtime.core.api.lifecycle.LifecycleUtils.disposeIfNeeded;

public final class HttpRestPropagator implements Propagator, Initialisable , Disposable {
    private final Logger LOGGER = LoggerFactory.getLogger(HttpRestPropagator.class);

    private static HttpConnection httpConnection;
    private static final String NAME_PATTERN = "http.requester.%s";
    @Inject
    private HttpRequesterConnectionManager connectionManager;
    @RefName
    private String configName;
    @Inject
    private TransformationService transformationService;

    @Connection
    private Supplier<HttpConnection>  connectionSupplier;
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
    @Placement(tab = "Authentication")
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

    private HttpClientConfiguration getHttpClientConfiguration() {
        String name = String.format(NAME_PATTERN, configName);
        return new HttpClientConfiguration.Builder()
                .setTlsContextFactory(tlsContext)
                .setMaxConnections(-1)
                .setUsePersistentConnections(false)
                .setConnectionIdleTimeout(30000)
                .setName(name)
                .build();
    }

    @Override
    public void initialise() throws InitialisationException {
        try {
            java.util.Optional<HttpRequesterConnectionManager.ShareableHttpClient> client = connectionManager.lookup(configName);
            HttpRequesterConnectionManager.ShareableHttpClient httpClient = client.orElseGet(() -> connectionManager.create(configName, this.getHttpClientConfiguration()));
            httpConnection = new HttpConnection(httpClient, authentication,url);
            httpConnection.start();
        } catch (MuleException e) {
            LOGGER.warn("Failed to create httpClient - {}", e.getMessage());
        }
    }

    @Override
    public void dispose() {
        if (authentication != null) {
            disposeIfNeeded(authentication, LOGGER);
        }
    }

    public static Supplier<HttpConnection> connectionSupplier(){
      return () -> httpConnection;
    }
}
