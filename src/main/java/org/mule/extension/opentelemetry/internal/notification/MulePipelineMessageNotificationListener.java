package org.mule.extension.opentelemetry.internal.notification;

import org.mule.extension.opentelemetry.internal.OpenTelemetryConnection;
import org.mule.extension.opentelemetry.internal.exception.SpanException;
import org.mule.extension.opentelemetry.internal.interceptor.AbstractTracingHandler;
import org.mule.extension.opentelemetry.internal.service.ConnectionHolder;
import org.mule.extension.opentelemetry.trace.FlowSpan;
import org.mule.extension.opentelemetry.trace.SpanWrapper;
import org.mule.extension.opentelemetry.trace.Transaction;
import org.mule.extension.opentelemetry.util.OplUtils;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.notification.EnrichedNotificationInfo;
import org.mule.runtime.api.notification.PipelineMessageNotification;
import org.mule.runtime.api.notification.PipelineMessageNotificationListener;
import org.mule.runtime.api.util.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class MulePipelineMessageNotificationListener extends AbstractTracingHandler implements PipelineMessageNotificationListener<PipelineMessageNotification> {
    private final Logger LOGGER = LoggerFactory.getLogger(MulePipelineMessageNotificationListener.class);

    public MulePipelineMessageNotificationListener(ConnectionHolder<OpenTelemetryConnection> connectionHolder) {
        super(connectionHolder);
    }


    @Override
    public void onNotification(PipelineMessageNotification notification) {
        try {
            this.handler(notification);
        } catch (Exception e) {
            LOGGER.error("onNotification Interception", e);
        }

    }

    private void handler(PipelineMessageNotification notification) throws SpanException {
        OpenTelemetryConnection connectionHolderConnection = connectionHolder.getConnection();
        int action = Integer.parseInt(notification.getAction().getIdentifier());
        Event event = notification.getEvent();
        String eventId = event.getContext().getId();
        EnrichedNotificationInfo notificationInfo = notification.getInfo();
        ComponentLocation componentLocation = notificationInfo.getComponent().getLocation();
        if (action == PipelineMessageNotification.PROCESS_START) {
            LOGGER.trace("PROCESS_START - Flow  {} received - ContextId {} ", componentLocation, eventId);
            this.handler(componentLocation, event);
        }

        if (action == PipelineMessageNotification.PROCESS_COMPLETE) {
            LOGGER.trace("PROCESS_COMPLETE - Flow {} received - ContextId {}", notification, eventId);
            Exception exception = notification.getException();
            if (Objects.nonNull(exception)) {
                Optional<Transaction> transaction = connectionHolderConnection.getTraceCollector().endTransaction(eventId, componentLocation, exception);
                if (!transaction.isPresent()) {
                    LOGGER.warn("No transaction found with {}", eventId);
                }
                return;
            }
            Optional<Transaction> transaction = connectionHolderConnection.getTraceCollector().endTransaction(eventId, componentLocation);
            if (!transaction.isPresent()) {
                LOGGER.warn("No transaction found with {}", eventId);
            }
        }
    }


    @Override
    protected SpanWrapper createSpan(Event event, ComponentLocation componentLocation) {
        MultiMap<String, String> stringStringMap = getAttributes(event);
        FlowSpan span = new FlowSpan().setName(componentLocation.getRootContainerName()).setAttributes(stringStringMap);
        return OplUtils.createSpan(span, event.getContext().getId(), componentLocation);
    }


    protected MultiMap<String, String> getAttributes(Event event) {
        Map<String, TypedValue<?>> variables = event.getVariables();
        return variables.entrySet().stream().filter(entry -> {
            TypedValue<?> value = entry.getValue();
            return (DataType.TEXT_STRING.equals(value.getDataType()));
        }).collect(Collectors.toMap(Map.Entry::getKey, this::getValue, (s, s2) -> s2, MultiMap::new));
    }

    private String getValue(Map.Entry<String, TypedValue<?>> data) {
        TypedValue<?> value = data.getValue();
        return Objects.nonNull(value.getValue()) ? value.getValue().toString() : "null";
    }
}
