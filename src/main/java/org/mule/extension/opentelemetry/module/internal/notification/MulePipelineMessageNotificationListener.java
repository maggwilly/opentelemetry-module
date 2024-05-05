package org.mule.extension.opentelemetry.module.internal.notification;

import org.mule.extension.opentelemetry.module.internal.provider.TracingManager;
import org.mule.extension.opentelemetry.module.trace.Transaction;
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
        String contextId = notification.getEvent().getContext().getId();
        int action = Integer.parseInt(notification.getAction().getIdentifier());
        if(action == PipelineMessageNotification.PROCESS_START){
            LOGGER.info("PROCESS_START - Flow  received - ContextId {}", contextId);
        }
        if(action == PipelineMessageNotification.PROCESS_COMPLETE){
            LOGGER.info("PROCESS_COMPLETE - Flow {} received - ContextId {}",notification, contextId);
            Exception exception = notification.getException();
            if(Objects.nonNull(exception)){
                Optional<Transaction> transaction = tracingManager.closeTransaction(contextId, exception);
                if(!transaction.isPresent()){
                    LOGGER.info("No transaction found with {}", contextId);
                }
                return;
            }
            Optional<Transaction> transaction = tracingManager.closeTransaction(contextId);
            if(!transaction.isPresent()){
                LOGGER.info("No transaction found with {}", contextId);
            }
        }
    }
}
