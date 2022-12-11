package com.ee.notificationservice.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class kafkaServiceProperties {

    @Value("${spring.kafka.template.default-topic}")
    private String inBoundTopic;

    @Value("${topics.producer-topic}")
    private String outBoundTopic;

}
