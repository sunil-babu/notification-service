package com.ee.notificationservice.serdes;

import com.ee.notificationservice.payload.OrderEvent;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class OrderSerdes extends Serdes.WrapperSerde<OrderEvent<?>> {

    public OrderSerdes() {
        super(new JsonSerializer<>(), new JsonDeserializer<>(OrderEvent.class));
    }

    public static Serde<OrderEvent> serdes() {
        JsonSerializer<OrderEvent> serializer = new JsonSerializer<>();
        JsonDeserializer<OrderEvent> deserializer = new JsonDeserializer<>(OrderEvent.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

}