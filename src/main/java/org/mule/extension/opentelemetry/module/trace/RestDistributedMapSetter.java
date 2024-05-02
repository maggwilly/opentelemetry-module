package org.mule.extension.opentelemetry.module.trace;

import com.google.common.base.Strings;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.mule.extension.opentelemetry.module.internal.connection.ConnectionManagementStrategy;
import org.mule.extension.opentelemetry.module.internal.http.HttpConnection;
import org.mule.extension.opentelemetry.module.internal.http.HttpRequestBuilder;
import org.mule.extension.opentelemetry.module.internal.http.HttpUtils;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionHandler;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.transformation.TransformationService;
import org.mule.runtime.http.api.HttpConstants;
import org.mule.runtime.http.api.domain.entity.ByteArrayHttpEntity;
import org.mule.runtime.http.api.domain.entity.HttpEntity;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

import static org.mule.extension.opentelemetry.module.internal.http.HttpConstants.CONTENT_TYPE_HEADER;
import static org.mule.extension.opentelemetry.module.internal.http.HttpConstants.RESPONSE_TIMEOUT;

public class RestDistributedMapSetter  implements TextMapSetter<Map<String, String>> {
    private final Logger LOGGER = LoggerFactory.getLogger(RestDistributedMapSetter.class);
    private final ConnectionManagementStrategy<HttpConnection> managementStrategy;
    private final TransformationService transformationService;

    public RestDistributedMapSetter(ConnectionManagementStrategy<HttpConnection> managementStrategy, TransformationService transformationService) {
        this.managementStrategy = managementStrategy;
        this.transformationService = transformationService;
    }


    @Override
    public void set(Map<String, String> carrier, String key, String value) {
        LOGGER.trace("Setting key {} value{}", key, value);
        if (Objects.nonNull(carrier) && !Strings.isNullOrEmpty(key)) {
            carrier.put(key, value);
            String id = String.format("%s_%s",carrier.get(DistributedContextPropagator.CONTEXT_ID_KEY),key) ;
            try {
                doRemoteSet(value, id);
            } catch (Exception e) {
                LOGGER.error("Error setting {} - {}", key, e.getMessage());
            }
        }
    }

    private void doRemoteSet(String value, String id) throws ConnectionException {
        ConnectionHandler<HttpConnection> connectionHandler = managementStrategy.getConnectionHandler();
        HttpConnection httpConnection = connectionHandler.getConnection();
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder(true);
        TypedValue<String> stringTypedValue = new TypedValue<>(value, DataType.JSON_STRING);
        HttpEntity requestEntity = createRequestEntity(httpRequestBuilder, stringTypedValue);
        HttpRequest httpRequest = createRequest(httpRequestBuilder, requestEntity, String.format("%s/%s", httpConnection.getUrl(), id));
        httpConnection.send(httpRequest, RESPONSE_TIMEOUT, false, HttpUtils.resolveAuthentication(httpConnection.getDefaultAuthentication()));
    }

    private HttpRequest createRequest(HttpRequestBuilder httpRequestBuilder, HttpEntity requestEntity, String url) {
        return httpRequestBuilder.uri(url).method(HttpConstants.Method.POST).entity(requestEntity).build();
    }

    private byte[] getPayloadAsBytes(Object payload, TransformationService transformationService) {
        return (byte[]) transformationService.transform(Message.of(payload), DataType.BYTE_ARRAY).getPayload().getValue();
    }


    private HttpEntity createRequestEntity(HttpRequestBuilder httpRequestBuilder, TypedValue<?> body) {
        Object payload = body.getValue();
        MediaType mediaType = body.getDataType().getMediaType();
        httpRequestBuilder.addHeader(CONTENT_TYPE_HEADER, mediaType.toRfcString());
        byte[] payloadAsBytes = this.getPayloadAsBytes(payload, transformationService);
        return new ByteArrayHttpEntity(payloadAsBytes);
    }

}
