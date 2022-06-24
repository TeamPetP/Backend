package petPeople.pet.controller.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.notification.entity.Notification;
import petPeople.pet.domain.notification.service.NotificationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    //알림 삭제
    @DeleteMapping("{notificationId}")
    public ResponseEntity deleteNotification(@PathVariable Long notificationId,
                                             Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        notificationService.deleteNotification(notificationId, member);
        return ResponseEntity.noContent().build();
    }
}
