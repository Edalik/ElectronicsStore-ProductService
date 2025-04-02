package ru.edalik.electronics.store.product.service.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.edalik.electronics.store.product.service.config.KafkaConfig;
import ru.edalik.electronics.store.product.service.model.dto.NotificationRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final KafkaConfig kafkaConfig;

    private final ObjectMapper objectMapper;

    public void sendMessage(NotificationRequest message) {
        log.info("Start sending message to {}: {}", kafkaConfig.getNotificationTopic(), message);
        try {
            kafkaTemplate.send(
                kafkaConfig.getNotificationTopic(),
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(message)
            );
        } catch (Exception e) {
            log.error("Error while sending message to {}: {}", kafkaConfig.getNotificationTopic(), message, e);
        }
    }

}