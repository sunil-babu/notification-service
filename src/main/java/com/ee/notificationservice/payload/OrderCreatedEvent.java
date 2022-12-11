package com.ee.notificationservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderCreatedEvent implements Serializable {

    private Integer customerId;
    private String customerName;
    private LocalDateTime creationDateTime;
    private Integer orderId;
    private List<OrderRow> orderRows;

    public Integer getItemCount(){
        return orderRows.stream().mapToInt(OrderRow::getQuantity).sum();
    }
    public BigDecimal getTotalSum() {

        return  orderRows.stream().map(orderRow -> orderRow.getUnitaryPrice().multiply(BigDecimal.valueOf(orderRow.getQuantity())))
            .reduce(BigDecimal.ZERO,BigDecimal::add);
    }

}
