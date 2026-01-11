package com.org.emprunt.service;

import com.org.emprunt.dto.EmpruntDetailsDTO;
import com.org.emprunt.entities.Emprunter;
import com.org.emprunt.feign.BookClient;
import com.org.emprunt.feign.UserClient;
import com.org.emprunt.repositories.EmpruntRepository;
import com.org.emprunt.kafka.EmpruntProducer;
import com.org.emprunt.dto.EmpruntEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmpruntService {

    private final EmpruntRepository repo;
    private final UserClient userClient;
    private final BookClient bookClient;
    private final EmpruntProducer empruntProducer;

    public EmpruntService(EmpruntRepository repo, UserClient userClient, BookClient bookClient, EmpruntProducer empruntProducer) {
        this.repo = repo;
        this.userClient = userClient;
        this.bookClient = bookClient;
        this.empruntProducer = empruntProducer;
    }

    public Emprunter createEmprunt(Long userId, Long bookId) {

        // 1. Vérifier user existe
        userClient.getUser(userId);
        log.info("✅ Utilisateur {} vérifié", userId);

        // 2. Vérifier book existe
        bookClient.getBook(bookId);
        log.info("✅ Livre {} vérifié", bookId);

        // 3. Créer l'emprunt
        Emprunter emprunt = new Emprunter();
        emprunt.setUserId(userId);
        emprunt.setBookId(bookId);
        emprunt.setEmpruntDate(LocalDateTime.now());

        // 4. Sauvegarder l'emprunt
        Emprunter savedEmprunt = repo.save(emprunt);
        log.info("💾 Emprunt {} créé avec succès", savedEmprunt.getId());

        // 5. Envoyer l'événement Kafka
        try {
            EmpruntEvent event = new EmpruntEvent(
                savedEmprunt.getId(),
                userId,
                bookId,
                "EMPRUNT_CREATED",
                LocalDateTime.now()
            );
            
            empruntProducer.sendEmpruntEvent(event);
            log.info("📨 Événement Kafka envoyé pour l'emprunt {}", savedEmprunt.getId());
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de l'événement Kafka: {}", e.getMessage());
            // L'emprunt est déjà créé, on ne fait que logger l'erreur
        }

        return savedEmprunt;
    }

    public List<EmpruntDetailsDTO> getAllEmprunts() {
        return repo.findAll().stream().map(e -> {

            var user = userClient.getUser(e.getUserId());
            var book = bookClient.getBook(e.getBookId());

            return new EmpruntDetailsDTO(
                    e.getId(),
                    user.getName(),
                    book.getTitle(),
                    e.getEmpruntDate().toLocalDate()  // ✅ CORRECTION: Conversion LocalDateTime → LocalDate
            );
        }).collect(Collectors.toList());
    }

}