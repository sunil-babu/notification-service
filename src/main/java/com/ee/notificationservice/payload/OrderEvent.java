package com.ee.notificationservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvent<T> implements Serializable {

    private String eventId;

    private String eventName;

    private LocalDateTime eventDate;

    private T data;

}
