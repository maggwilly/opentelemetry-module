package org.mule.extension.opentelemetry.module.trace;

import io.opentelemetry.context.propagation.TextMapGetter;
import org.mule.extension.opentelemetry.module.internal.http.HttpConnection;
import org.mule.extension.opentelemetry.module.internal.http.HttpRequestBuilder;
import org.mule.extension.opentelemetry.module.internal.http.HttpUtils;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.transformation.TransformationService;
import org.mule.runtime.http.api.HttpConstants;
import org.mule.runtime.http.api.domain.entity.EmptyHttpEntity;
import org.mule.runtime.http.api.domain.entity.HttpEntity;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static org.mule.extension.opentelemetry.module.internal.http.HttpConstants.RESPONSE_TIMEOUT;

public class RestDistributedMapGetter implements TextMapGetter<Map<String, String>> {
    private final Logger LOGGER = LoggerFactory.getLogger(RestDistributedMapSetter.class);
    private final Supplier<HttpConnection> connectionSupplier;

    public RestDistributedMapGetter(Supplier<HttpConnection> connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
    }

    @Override
    public Iterable<String> keys(Map<String, String> carrier) {
        return carrier.keySet();
    }

    @Nullable
    @Override
    public String get(@Nullable Map<String, String> carrier, String key) {
        if (Objects.nonNull(carrier)) {
            String value = carrier.get(key);
            if (Objects.nonNull(value)) {
                return value;
            }
            try {
                String id = String.format("%s_%s", carrier.get(DistributedContextPropagator.CONTEXT_ID_KEY), key);
                CompletableFuture<HttpResponse> httpResponseCompletableFuture = doRemoteGet(id);
                byte[] bytes = httpResponseCompletableFuture.get().getEntity().getBytes();
                return new String(bytes);
            } catch (IOException | InterruptedException | ExecutionException e) {
                LOGGER.error("Error getting {} - {}", key, e.getMessage());
                return "";
            }
        }

        return null;
    }

    private CompletableFuture<HttpResponse> doRemoteGet(String id) throws IOException {
        HttpConnection httpConnection = connectionSupplier.get();
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder(true);
        HttpEntity requestEntity = new EmptyHttpEntity();
        HttpRequest httpRequest = createRequest(httpRequestBuilder, requestEntity, String.format("%s/%s", httpConnection.getUrl(), id));
        return httpConnection.send(httpRequest, RESPONSE_TIMEOUT, false, HttpUtils.resolveAuthentication(httpConnection.getDefaultAuthentication()));
    }

    private HttpRequest createRequest(HttpRequestBuilder httpRequestBuilder, HttpEntity requestEntity, String url) {
        return httpRequestBuilder.uri(url).method(HttpConstants.Method.GET).entity(requestEntity).build();
    }
}
