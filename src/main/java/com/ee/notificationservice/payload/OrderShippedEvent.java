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
public class OrderShippedEvent implements Serializable {

    private Integer orderId;
    private LocalDateTime shippingDate;
    private Integer deliveryCompanyId;
    private String deliveryCompanyName;
    private BigDecimal shippingCost;
    private Integer wareHousePosition;
}
