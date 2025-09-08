package com.example.board.alarm;

import com.example.board.security.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationRepository notificationRepository;

    public void sendNotification(User receiver, User sender, String content, NotificationType type) {
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

        Notification notification = new Notification();
        notification.setReceiver(receiver);
        notification.setSender(sender);
        notification.setContent(preview);
        notification.setNotificationType(type);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        String formattedTime = notification.getCreatedAt()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        NotificationMessageDto message = new NotificationMessageDto(
                sender.getEmail(),
                preview,
                type,
                formattedTime
        );

        //인메모리 자동 라우팅 방식
        //대규모 서비스 일때는 kafka, redis, rabbitMQ 메시지 브로커 이용
        simpMessagingTemplate.convertAndSendToUser(
                receiver.getEmail(),
                "/queue/notifications",
                message
        );
    }
}
