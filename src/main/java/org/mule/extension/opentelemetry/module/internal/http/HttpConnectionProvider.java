package org.mule.extension.opentelemetry.module.internal.http;


import org.mule.extension.opentelemetry.module.trace.HttpRestPropagator;
import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.exception.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

import static org.mule.runtime.api.connection.ConnectionValidationResult.success;

public class HttpConnectionProvider implements CachedConnectionProvider<Supplier<HttpConnection>> {

    private final Logger LOGGER = LoggerFactory.getLogger(HttpConnectionProvider.class);

    @Override
    public Supplier<HttpConnection> connect() throws ConnectionException {
        return HttpRestPropagator.connectionSupplier();
    }


    @Override
    public void disconnect(Supplier<HttpConnection> httpClient) {
        try {
            httpClient.get().stop();
        } catch (MuleException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Found exception trying to stop http client: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public ConnectionValidationResult validate(Supplier<HttpConnection> httpClient) {
        return success();
    }

}
