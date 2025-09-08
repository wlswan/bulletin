package com.example.board.alarm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessageDto {
    private String sender;
    private String preview;
    private NotificationType notificationType;
    private String createdAt;
}
