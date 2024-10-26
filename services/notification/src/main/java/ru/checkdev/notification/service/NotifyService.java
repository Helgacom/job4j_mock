package ru.checkdev.notification.service;

import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.checkdev.notification.domain.Notify;

@Service
@AllArgsConstructor
public class NotifyService {

    private final TemplateService templates;

    @KafkaListener(topics = "notify")
    public void send(Notify notify) {
        this.templates.send(notify);
    }
}
