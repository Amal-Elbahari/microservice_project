package com.example.notificationservice.service;

import com.example.notificationservice.dto.EmpruntEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void sendNotification(EmpruntEvent event) {
        log.info("============================================================");
        log.info(" NOTIFICATION - NOUVEL EMPRUNT CRÉÉ");
        log.info("============================================================");
        log.info("ID Emprunt    : {}", event.getEmpruntId());
        log.info("ID Utilisateur: {}", event.getUserId());
        log.info(" ID Livre      : {}", event.getBookId());
        log.info("Date          : {}", event.getTimestamp());
        log.info("Type          : {}", event.getEventType());
        log.info("============================================================");
        
        System.out.println("✅ Notification envoyée avec succès!");
        log.info("✅ Notification traitée pour l'emprunt ID: {}", event.getEmpruntId());
    }
}