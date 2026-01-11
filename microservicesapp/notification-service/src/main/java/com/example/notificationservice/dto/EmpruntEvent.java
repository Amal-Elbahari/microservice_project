package com.example.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpruntEvent {
    private Long empruntId;
    private Long userId;
    private Long bookId;
    private String eventType;
    private LocalDateTime timestamp;
}