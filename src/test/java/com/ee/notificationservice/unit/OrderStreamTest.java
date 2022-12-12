package com.ee.notificationservice.unit;

import com.ee.notificationservice.mapper.OrderMapper;
import com.ee.notificationservice.payload.OrderEvent;
import com.ee.notificationservice.properties.kafkaServiceProperties;
import com.ee.notificationservice.streams.OrderStream;
import com.ee.notificationservice.serdes.OrderSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Properties;

import static com.ee.notificationservice.util.TestEventData.orderCreatedEvent;
import static com.ee.notificationservice.util.TestEventData.orderShippedEvent;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
public class OrderStreamTest {

    @Autowired
    private OrderStream orderStream;

    private kafkaServiceProperties properties;
    private OrderMapper orderMapper;
    private static final String ORDER_INBOUND_TOPIC = "order";
    private static final String ORDER_OUTBOUND_TOPIC = "acme-order";


    @BeforeEach
    void setUp() {
        properties = mock(kafkaServiceProperties.class);
        orderMapper = new OrderMapper();
        when(properties.getInBoundTopic()).thenReturn(ORDER_INBOUND_TOPIC);
        when(properties.getOutBoundTopic()).thenReturn(ORDER_OUTBOUND_TOPIC);
        orderStream = new OrderStream(orderMapper,properties);
    }

    @Test
    void testOrderCreatedTopology() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        orderStream.buildPipeline(streamsBuilder);
        Topology topology = streamsBuilder.build();

        Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(DEFAULT_KEY_SERDE_CLASS_CONFIG,   Serdes.String().getClass().getName());
        streamsConfiguration.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());

        TopologyTestDriver topologyTestDriver = new TopologyTestDriver(topology, streamsConfiguration);
        OrderEvent orderEvents = orderCreatedEvent();
        TestInputTopic<String, OrderEvent> inputTopic = topologyTestDriver
            .createInputTopic(ORDER_INBOUND_TOPIC, new StringSerializer(), OrderSerdes.serdes().serializer());

        TestOutputTopic<String, OrderEvent> orderOutputTopic = topologyTestDriver
            .createOutputTopic(ORDER_OUTBOUND_TOPIC, new StringDeserializer(), OrderSerdes.serdes().deserializer());


        assertThat(orderOutputTopic.isEmpty(),is(true));
        inputTopic.pipeInput(orderEvents.getEventId(), orderEvents);


        // Assert the outbound topics have the expected events.
        assertThat(orderOutputTopic.isEmpty(),is(false));
        assertThat(orderOutputTopic.readValue().getEventId(),equalTo(orderEvents.getEventId()));
    }

    @Test
    void testOrderShippedTopology() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        orderStream.buildPipeline(streamsBuilder);
        Topology topology = streamsBuilder.build();

        Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(DEFAULT_KEY_SERDE_CLASS_CONFIG,   Serdes.String().getClass().getName());
        streamsConfiguration.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());

        TopologyTestDriver topologyTestDriver = new TopologyTestDriver(topology, streamsConfiguration);
        OrderEvent orderEvents = orderShippedEvent();
        TestInputTopic<String, OrderEvent> inputTopic = topologyTestDriver
            .createInputTopic(ORDER_INBOUND_TOPIC, new StringSerializer(), OrderSerdes.serdes().serializer());

        TestOutputTopic<String, OrderEvent> orderOutputTopic = topologyTestDriver
            .createOutputTopic(ORDER_OUTBOUND_TOPIC, new StringDeserializer(), OrderSerdes.serdes().deserializer());


        assertThat(orderOutputTopic.isEmpty(),is(true));
        inputTopic.pipeInput(orderEvents.getEventId(), orderEvents);


        // Assert the outbound topics have the expected events.
        assertThat(orderOutputTopic.isEmpty(),is(false));
        assertThat(orderOutputTopic.readValue().getEventId(),equalTo(orderEvents.getEventId()));
    }

}
