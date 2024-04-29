package org.mule.extension.opentelemetry.module.internal.notification;

import org.mule.runtime.api.notification.PipelineMessageNotification;
import org.mule.runtime.api.notification.PipelineMessageNotificationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MulePipelineMessageNotificationListener implements PipelineMessageNotificationListener<PipelineMessageNotification> {
    private final Logger LOGGER = LoggerFactory.getLogger(MulePipelineMessageNotificationListener.class);

    @Override
    public void onNotification(PipelineMessageNotification notification) {
        LOGGER.info("Event name {}", notification.getEventName());
    }
}
