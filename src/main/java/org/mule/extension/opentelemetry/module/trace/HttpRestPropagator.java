package org.mule.extension.opentelemetry.module.trace;

import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.mule.extension.http.api.request.authentication.HttpRequestAuthentication;
import org.mule.extension.opentelemetry.module.internal.connection.CachedConnectionHandler;
import org.mule.extension.opentelemetry.module.internal.connection.CachedConnectionManagementStrategy;
import org.mule.extension.opentelemetry.module.internal.connection.ConnectionManagementStrategy;
import org.mule.extension.opentelemetry.module.internal.http.HttpConnection;
import org.mule.extension.opentelemetry.module.internal.http.HttpRequesterConnectionManager;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Disposable;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.tls.TlsContextFactory;
import org.mule.runtime.api.transformation.TransformationService;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.lifecycle.LifecycleUtils;
import org.mule.runtime.core.internal.connection.ConnectionHandlerAdapter;
import org.mule.runtime.extension.api.annotation.Expression;
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

import java.util.Map;

import static org.mule.runtime.api.connection.ConnectionValidationResult.success;
import static org.mule.runtime.core.api.lifecycle.LifecycleUtils.initialiseIfNeeded;
public final class HttpRestPropagator implements ConnectionProvider<HttpConnection>, Propagator, Initialisable , Disposable{
    private final Logger LOGGER = LoggerFactory.getLogger(HttpRestPropagator.class);

    private static final String NAME_PATTERN = "http.requester.%s";
    @Inject
    private HttpRequesterConnectionManager connectionManager;
    @RefName
    private String configName;
    @Inject
    protected TransformationService transformationService;
    @Inject
    private MuleContext muleContext;
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
    private CachedConnectionManagementStrategy<HttpConnection> connectionManagementStrategy;


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
        this.connectionManagementStrategy = new CachedConnectionManagementStrategy<>(this, muleContext);
        if (tlsContext != null) {
            initialiseIfNeeded(tlsContext);
        }
    }
    @Override
    public HttpConnection connect() throws ConnectionException {
        java.util.Optional<HttpRequesterConnectionManager.ShareableHttpClient> client = this.connectionManager.lookup(this.configName);
        HttpRequesterConnectionManager.ShareableHttpClient httpClient = client.orElseGet(() -> this.connectionManager.create(this.configName, this.getHttpClientConfiguration()));
        HttpConnection httpConnection = new HttpConnection(httpClient, authentication, url);
        try {
            httpConnection.start();
        } catch (MuleException e) {
            throw new ConnectionException(e);
        }

        return httpConnection;
    }


    @Override
    public void disconnect(HttpConnection httpClient) {
        try {
            httpClient.stop();
        } catch (MuleException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Found exception trying to stop http client: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void dispose() {
        try {
            if (this.authentication != null) {
                LifecycleUtils.disposeIfNeeded(this.authentication, LOGGER);
            }
            connectionManagementStrategy.close();
        } catch (MuleException e) {
            LOGGER.warn("Found exception: " + e.getMessage(), e);

        }
    }

    @Override
    public ConnectionValidationResult validate(HttpConnection httpExtensionClient) {
        return success();
    }

    @Override
    public TextMapGetter<Map<String, String>> getter() {
        return new RestDistributedMapGetter(connectionManagementStrategy);
    }

    @Override
    public TextMapSetter<Map<String, String>> setter() {
        return new RestDistributedMapSetter(connectionManagementStrategy, transformationService);
    }
}
