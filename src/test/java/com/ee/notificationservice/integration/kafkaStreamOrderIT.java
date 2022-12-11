package com.ee.notificationservice.integration;

import com.ee.notificationservice.mapper.JsonMapper;
import com.ee.notificationservice.payload.OrderCreatedEvent;
import com.ee.notificationservice.payload.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.ee.notificationservice.util.TestEventData.orderCreatedEvent;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@EmbeddedKafka(controlledShutdown = true, topics = { "order", "acme-order" },partitions = 1)
@TestPropertySource(properties = { "spring.config.location=classpath:application.yml" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
public class kafkaStreamOrderIT {

    @Autowired
    private KafkaTemplate<String,OrderEvent> testKafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @Autowired
    private KafkaAcmeOrderListener acmeOrderReceiver;

    @Configuration
    static class TestConfig {

        @Bean
        public KafkaAcmeOrderListener acmeOrderReceiver() {
            return new KafkaAcmeOrderListener();
        }

    }


    public static class KafkaAcmeOrderListener {
        AtomicInteger counter = new AtomicInteger(0);
        AtomicLong total = new AtomicLong(0);

        @KafkaListener(groupId = "KafkaStreamsIntegrationTest", topics = "acme-order", autoStartup = "true")
        void receive(@Payload final OrderEvent payload) {
            OrderCreatedEvent orderCreatedEvent = JsonMapper.convertValue(payload.getData(), OrderCreatedEvent.class);
            total.addAndGet(orderCreatedEvent.getTotalSum().longValue());
            counter.incrementAndGet();
        }
    }


    @BeforeEach
    public void setUp() {
        // Wait until the partitions are assigned.
        registry.getListenerContainers().forEach(container ->
            ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic()));

        acmeOrderReceiver.counter.set(0);

    }


    @Test
    @Timeout(10)
    public void testKafkaStreams() throws Exception {

        //Given One ORDER_CREATED Event with total sum 20L
        sendMessage("acme-order",orderCreatedEvent());

        await().atMost(10, TimeUnit.SECONDS).pollDelay(100, TimeUnit.MILLISECONDS)
            .until(acmeOrderReceiver.counter::get, equalTo(1));

        // Assert total amounts received for the order.
        assertThat(acmeOrderReceiver.total.get(), equalTo(20L));


    }


    /**
     * Send the given Order event to the given topic.
     */
    private void sendMessage(String topic, OrderEvent event) throws Exception {

        final ProducerRecord<String, OrderEvent> record = new ProducerRecord(topic, null, event.getEventId(), event);

        testKafkaTemplate.send(record).get();
    }


}
