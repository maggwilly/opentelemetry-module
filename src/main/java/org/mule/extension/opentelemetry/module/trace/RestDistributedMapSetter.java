package org.mule.extension.opentelemetry.module.trace;

import com.google.common.base.Strings;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.mule.extension.opentelemetry.module.internal.http.HttpConnection;
import org.mule.extension.opentelemetry.module.internal.http.HttpRequestBuilder;
import org.mule.extension.opentelemetry.module.internal.http.HttpUtils;
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
import java.util.function.Supplier;

import static org.mule.extension.opentelemetry.module.internal.http.HttpConstants.CONTENT_TYPE_HEADER;
import static org.mule.extension.opentelemetry.module.internal.http.HttpConstants.RESPONSE_TIMEOUT;

public class RestDistributedMapSetter  implements TextMapSetter<Map<String, String>> {
    private final Logger LOGGER = LoggerFactory.getLogger(RestDistributedMapSetter.class);
    private final Supplier<HttpConnection> connectionSupplier;
    private final TransformationService transformationService;

    public RestDistributedMapSetter(Supplier<HttpConnection> connectionSupplier, TransformationService transformationService) {
        this.connectionSupplier = connectionSupplier;
        this.transformationService = transformationService;
    }


    @Override
    public void set(Map<String, String> carrier, String key, String value) {
        LOGGER.trace("Setting key {} value{}", key, value);
        if (Objects.nonNull(carrier) && !Strings.isNullOrEmpty(key)) {
            carrier.put(key, value);
            String id = String.format("%s_%s",carrier.get(DistributedContextPropagator.CONTEXT_ID_KEY),key) ;
            doRemoteSet(value, id);
        }
    }

    private void doRemoteSet(String value, String id) {
        HttpConnection httpConnection = connectionSupplier.get();
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
