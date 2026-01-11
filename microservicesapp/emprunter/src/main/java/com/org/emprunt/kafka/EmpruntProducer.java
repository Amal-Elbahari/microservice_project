package com.org.emprunt.kafka;

import com.org.emprunt.dto.EmpruntEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmpruntProducer {
    
    private static final String TOPIC = "emprunt-created";
    
    private final KafkaTemplate<String, EmpruntEvent> kafkaTemplate;
    
    public void sendEmpruntEvent(EmpruntEvent event) {
        log.info("📤 Envoi de l'événement vers Kafka topic '{}': {}", TOPIC, event);
        kafkaTemplate.send(TOPIC, event);
    }
}