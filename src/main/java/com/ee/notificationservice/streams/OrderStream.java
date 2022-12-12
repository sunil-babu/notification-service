package com.ee.notificationservice.streams;

import com.ee.notificationservice.constants.OrderEventType;
import com.ee.notificationservice.mapper.JsonMapper;
import com.ee.notificationservice.mapper.OrderMapper;
import com.ee.notificationservice.payload.OrderCreatedEvent;
import com.ee.notificationservice.payload.OrderEvent;
import com.ee.notificationservice.payload.OrderShippedEvent;
import com.ee.notificationservice.properties.kafkaServiceProperties;
import com.ee.notificationservice.serdes.OrderSerdes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderStream.class);
    private OrderMapper orderMapper;
    private static final Serde<String> STRING_SERDE = Serdes.String();

    private final kafkaServiceProperties kafkaProperties ;

    @Autowired
    public OrderStream(OrderMapper orderMapper,kafkaServiceProperties kafkaProperties){
        this.orderMapper = orderMapper;
        this.kafkaProperties = kafkaProperties;
    }
    @Autowired
    public void buildPipeline(StreamsBuilder streamsBuilder) {
        KStream<String, OrderEvent> messageStream = streamsBuilder
            .stream(kafkaProperties.getInBoundTopic(), Consumed.with(STRING_SERDE, OrderSerdes.serdes()))
            .peek((key, order) -> LOGGER.info("Order event received with key {} and order {} ", key ,  order))
            .mapValues(order -> {
                if (order.getEventName().equals(OrderEventType.ORDER_CREATED.name())) {
                    OrderCreatedEvent orderCreatedEvent = JsonMapper.convertValue(order.getData(), OrderCreatedEvent.class);
                    order.setData(orderMapper.fromOrderCreatedEvent(orderCreatedEvent));
                } else if (order.getEventName().equals(OrderEventType.ORDER_SHIPPED.name())) {
                    OrderShippedEvent orderShippedEvent = JsonMapper.convertValue(order.getData(), OrderShippedEvent.class);
                    order.setData(orderMapper.fromOrderShippedEvent(orderShippedEvent));
                }
                return order;
            })
            .peek((key, value) -> LOGGER.info("Mapped order event received with key {} key and value {}" ,key, value));

        // Publish outbound events.
        messageStream.to(kafkaProperties.getOutBoundTopic(), Produced.with(STRING_SERDE, OrderSerdes.serdes()));
    }

}
