package ru.checkdev.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.checkdev.notification.domain.Notify;

@Service
public class NotificationService {

    private final KafkaTemplate<String, Notify> kafkaTemplate;

    @Autowired
    public NotificationService(final KafkaTemplate<String, Notify> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void put(final Notify notify) {
        kafkaTemplate.send("notify", notify);
    }
}
