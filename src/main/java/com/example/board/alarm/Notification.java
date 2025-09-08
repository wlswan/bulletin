package com.example.board.alarm;

import com.example.board.security.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id" ,nullable = false)
    private User sender;

    private String content;

    @Enumerated(value = EnumType.STRING)
    private NotificationType notificationType;

    private boolean isRead = false;

    private LocalDateTime createdAt;



}
