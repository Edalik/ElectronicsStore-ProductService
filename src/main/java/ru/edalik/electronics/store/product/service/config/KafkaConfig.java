package ru.edalik.electronics.store.product.service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.topic.notification}")
    private String notificationTopic;

}