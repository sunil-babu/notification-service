package com.ee.notificationservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderShippedMessage implements Serializable {

    private Integer orderId;
    private LocalDateTime shippingDate;
    private String deliverCompanyName;
    private BigDecimal shippingCost;
}
