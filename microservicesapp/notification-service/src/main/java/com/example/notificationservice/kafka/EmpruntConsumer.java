package com.example.notificationservice.kafka;

import com.example.notificationservice.dto.EmpruntEvent;
import com.example.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmpruntConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "emprunt-created", groupId = "notification-group")
    public void consumeEmpruntEvent(EmpruntEvent event) {
        log.info("📨 Événement Kafka reçu: {}", event);
        notificationService.sendNotification(event);
    }
}