package com.org.emprunt.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "emprunts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Emprunter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Long bookId;
    
    @Column(name = "emprunt_date", nullable = false)
    private LocalDateTime empruntDate;
    
    @Column(name = "return_date")
    private LocalDateTime returnDate;
    
    @Column(nullable = false)
    private String status = "ACTIVE"; // ACTIVE, RETURNED
    
    // Constructeur personnalisé pour initialiser la date d'emprunt
    public Emprunter(Long userId, Long bookId) {
        this.userId = userId;
        this.bookId = bookId;
        this.empruntDate = LocalDateTime.now();
        this.status = "ACTIVE";
    }
}