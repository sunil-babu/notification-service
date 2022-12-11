package com.ee.notificationservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderRow implements Serializable {

    private Integer orderRowId;
    private Integer itemId;
    private Integer quantity;
    private BigDecimal unitaryPrice;
}
