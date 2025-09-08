package com.example.board.alarm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendNotification(String receiver, String sender, String content, NotificationType type) {
        String preview;
        if (type == NotificationType.COMMENT) {
            preview = (content != null && content.length() > 20) ?
                    content.substring(0, 20) + "..."
                    :content;
        } else if (type == NotificationType.LIKE) {
            preview = "게시글에 좋아요를 눌렀습니다.";
        } else {
            preview = "";
        }


        String formattedTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        NotificationMessageDto message = new NotificationMessageDto(
                sender,
                preview,
                type,
                formattedTime
        );

        simpMessagingTemplate.convertAndSendToUser(
                receiver,
                "/queue/notifications",
                message
        );
    }
}
