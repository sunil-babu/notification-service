package com.ee.notificationservice.listener;

import com.ee.notificationservice.exception.EventIdNotFoundException;
import com.ee.notificationservice.payload.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.FixedDelayStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventsListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventsListener.class);

    @RetryableTopic(
        attempts = "5",
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
        fixedDelayTopicStrategy = FixedDelayStrategy.SINGLE_TOPIC,
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        exclude = {SerializationException.class, DeserializationException.class,EventIdNotFoundException.class},
        dltStrategy = DltStrategy.FAIL_ON_ERROR

    )
    @KafkaListener(id = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.template.default-topic}" )
    public void handleMessage(@Payload OrderEvent<?> orderEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                              @Header(KafkaHeaders.OFFSET) int offset) {
        LOGGER.info("Received message: {} from topic: {} ,partition : {} , offset : {} ", orderEvent.toString(), topic,partition,offset);
        if(orderEvent.getEventId() == null) {
            throw new EventIdNotFoundException();
        }
    }

    @DltHandler
    public void handleDlt(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Message: {} handled by dlq topic: {}", message, topic);
    }
}

