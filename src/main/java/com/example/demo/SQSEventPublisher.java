package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

@Service
public class SQSEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQSEventPublisher.class);

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    public void publishEvent(String payload) {
        LOGGER.info("Generating event : {}", payload);
        try {
            GenericMessage<String> message = new GenericMessage<>(payload);
            queueMessagingTemplate.send("order-queue", message);
            LOGGER.info("Event has been published in SQS.");
        } catch (Throwable e) {
            LOGGER.error("Exception ocurred while pushing event to sqs : {} and stacktrace ; {}", e.getMessage(), e);
        }

    }

}