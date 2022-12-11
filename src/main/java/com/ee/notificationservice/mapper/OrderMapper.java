package com.ee.notificationservice.mapper;

import com.ee.notificationservice.payload.OrderCreatedEvent;
import com.ee.notificationservice.payload.OrderCreatedMessage;
import com.ee.notificationservice.payload.OrderShippedEvent;
import com.ee.notificationservice.payload.OrderShippedMessage;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderCreatedMessage fromOrderCreatedEvent(OrderCreatedEvent orderCreatedEvent){

        return new OrderCreatedMessage(orderCreatedEvent.getOrderId(),
            orderCreatedEvent.getCreationDateTime(),
            orderCreatedEvent.getItemCount(),
            orderCreatedEvent.getTotalSum());

    }

    public OrderShippedMessage fromOrderShippedEvent(OrderShippedEvent orderShippedEvent){

        return new OrderShippedMessage(orderShippedEvent.getOrderId(),
            orderShippedEvent.getShippingDate(),
            orderShippedEvent.getDeliveryCompanyName(),
            orderShippedEvent.getShippingCost());

    }

}
