package com.example.board.alarm;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);

}
