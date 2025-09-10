package com.example.board.alarm;

import com.example.board.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService ;

    @GetMapping
    public String getUnReadNotifications(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                         Model model) {
        Long receiverId = principalDetails.getUserId();
        List<Notification> unReadNotifications = notificationService.getUnReadNotifications(receiverId);
        model.addAttribute("notifications", unReadNotifications);
        return "notifications";

    }


    @PostMapping("/{id}/read")
    @ResponseBody
    public String readNotification(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "ok";
    }

}
