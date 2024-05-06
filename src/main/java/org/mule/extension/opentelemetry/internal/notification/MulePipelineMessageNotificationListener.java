package org.mule.extension.opentelemetry.internal.notification;

import org.mule.extension.opentelemetry.internal.provider.TracingManager;
import org.mule.extension.opentelemetry.trace.Transaction;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.notification.EnrichedNotificationInfo;
import org.mule.runtime.api.notification.PipelineMessageNotification;
import org.mule.runtime.api.notification.PipelineMessageNotificationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

public class MulePipelineMessageNotificationListener implements PipelineMessageNotificationListener<PipelineMessageNotification> {
    private final Logger LOGGER = LoggerFactory.getLogger(MulePipelineMessageNotificationListener.class);
    private final TracingManager tracingManager;

    public MulePipelineMessageNotificationListener(TracingManager tracingManager) {
        this.tracingManager = tracingManager;
    }

    @Override
    public void onNotification(PipelineMessageNotification notification) {
        int action = Integer.parseInt(notification.getAction().getIdentifier());
        String contextId = notification.getEvent().getContext().getId();
        EnrichedNotificationInfo notificationInfo = notification.getInfo();
        ComponentLocation componentLocation = notificationInfo.getComponent().getLocation();
        if(action == PipelineMessageNotification.PROCESS_START){
            LOGGER.trace("PROCESS_START - Flow  {} received - ContextId {} ",componentLocation, contextId);
            tracingManager.createTransaction(contextId, componentLocation);
        }
       if(action == PipelineMessageNotification.PROCESS_COMPLETE){
            LOGGER.trace("PROCESS_COMPLETE - Flow {} received - ContextId {}",notification, contextId);
           Exception exception = notification.getException();
            if(Objects.nonNull(exception)){
                Optional<Transaction> transaction = tracingManager.endTransaction(contextId,componentLocation, exception);
                if(!transaction.isPresent()){
                    LOGGER.warn("No transaction found with {}", contextId);
                }
                return;
            }
            Optional<Transaction> transaction = tracingManager.endTransaction(contextId, componentLocation);
            if(!transaction.isPresent()){
                LOGGER.warn("No transaction found with {}", contextId);
            }
        }
    }
}
