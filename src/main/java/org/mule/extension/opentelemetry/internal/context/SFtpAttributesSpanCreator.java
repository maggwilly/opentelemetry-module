package org.mule.extension.opentelemetry.internal.context;

import io.opentelemetry.context.Context;
import org.mule.extension.opentelemetry.internal.exception.ContextPropagatorException;
import org.mule.extension.opentelemetry.internal.exception.ParentContextException;
import org.mule.extension.opentelemetry.trace.ContextObjectStoreGetter;
import org.mule.extension.opentelemetry.util.OplUtils;
import org.mule.extension.sftp.api.SftpFileAttributes;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.api.i18n.I18nMessageFactory;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.store.ObjectStore;
import org.mule.runtime.api.util.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class SFtpAttributesSpanCreator extends AbstractSpanCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpHeadersSpanCreator.class);
    private final ContextManager contextManager;
    private final   ObjectStore<Serializable> objectStore;
    public SFtpAttributesSpanCreator(ContextManager contextManager, ObjectStore<Serializable> objectStore) {
        this.contextManager = contextManager;
        this.objectStore = objectStore;
    }

    public void extractParentContext(Event event) throws ParentContextException, ContextPropagatorException {
        if(Objects.nonNull(objectStore)) {
            try {
                TypedValue<SftpFileAttributes> attributes = event.getMessage().getAttributes();
                SftpFileAttributes attributesValue = attributes.getValue();
                ContextObjectStoreGetter textMapGetter = new ContextObjectStoreGetter(attributesValue.getName());
                Context traceContext = contextManager.getTraceContext(objectStore, textMapGetter);
                String parentTransactionId = OplUtils.getParentTransactionId(event.getContext().getId());
                contextManager.store(traceContext, parentTransactionId);
            }catch (Exception e){
                LOGGER.error("Failed to extract parent context from event {}", e.getMessage());
                throw new ParentContextException(I18nMessageFactory.createStaticMessage("Failed to extract parent context from event"));
            }
        }
        throw new ContextPropagatorException(I18nMessageFactory.createStaticMessage("No propagator provided for the tracing context"));
    }

    @Override
    protected MultiMap<String, String> getAttributes(Event event) {
        TypedValue<SftpFileAttributes> attributes = event.getMessage().getAttributes();
        SftpFileAttributes attr = attributes.getValue();
        MultiMap<String, String> tags = new MultiMap<>();
        tags.put("mule.event.correlationId", event.getCorrelationId());
        tags.put("file.name", attr.getName());
        tags.put("file.path", attr.getPath());
        tags.put("file.timestamp", attr.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME));
        tags.put("file.size", attr.getSize()+"");
        return tags;
    }
}
