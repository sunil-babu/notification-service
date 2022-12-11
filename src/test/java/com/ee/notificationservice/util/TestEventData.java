package com.ee.notificationservice.util;

import com.ee.notificationservice.constants.OrderEventType;
import com.ee.notificationservice.payload.OrderCreatedEvent;
import com.ee.notificationservice.payload.OrderEvent;
import com.ee.notificationservice.payload.OrderRow;
import com.ee.notificationservice.payload.OrderShippedEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestEventData {
    private static OrderCreatedEvent createOrderCreatedEvent(){
        List<OrderRow> orderRowList = new ArrayList<>();
        OrderRow orderRow = OrderRow.builder()
            .orderRowId(1)
            .itemId(1)
            .quantity(2)
            .unitaryPrice(BigDecimal.valueOf(10.11)).build();
        orderRowList.add(orderRow);
        return OrderCreatedEvent.builder()
            .orderId(1)
            .creationDateTime(LocalDateTime.now())
            .customerId(2)
            .customerName("dummy")
            .orderRows(orderRowList).build();
    }

    private static OrderShippedEvent createOrderShippedEvent(){
        return OrderShippedEvent.builder()
            .orderId(1)
            .shippingDate(LocalDateTime.now())
            .deliveryCompanyId(2)
            .deliveryCompanyName("dummy")
            .shippingCost(BigDecimal.valueOf(23.22))
            .wareHousePosition(45)
            .build();
    }

    public static OrderEvent<?> orderCreatedEvent(){
        return OrderEvent.builder()
            .eventId("123")
            .eventDate(LocalDateTime.now())
            .eventName(OrderEventType.ORDER_CREATED.name())
            .data(createOrderCreatedEvent()).build();
    }

    public static OrderEvent<?> orderShippedEvent(){
        return OrderEvent.builder()
            .eventId("123")
            .eventDate(LocalDateTime.now())
            .eventName(OrderEventType.ORDER_SHIPPED.name())
            .data(createOrderShippedEvent()).build();
    }
}
