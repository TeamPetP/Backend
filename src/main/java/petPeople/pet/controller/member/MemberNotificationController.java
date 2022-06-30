package petPeople.pet.controller.member;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import petPeople.pet.controller.member.dto.resp.notificationResp.MemberNotificationResponseDto;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.notification.service.NotificationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members/me/")
public class MemberNotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public ResponseEntity<Slice<MemberNotificationResponseDto>> getMemberNotifications(Pageable pageable,
                                                                                       Authentication authentication) {
        Long memberId = getMember(authentication).getId();
        return ResponseEntity.ok(notificationService.retrieveNotifications(memberId, pageable));
    }

    @PatchMapping("/notifications")
    public ResponseEntity updateMemberNotifications(Authentication authentication) {
        Long memberId = (getMember(authentication)).getId();
        notificationService.updateNotifications(memberId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/notifications")
    public ResponseEntity deleteAllNotification(Authentication authentication) {
        Long memberId = (getMember(authentication)).getId();
        notificationService.deleteAllNotification(memberId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/notifications/{notificationId}")
    public ResponseEntity updateMemberNotification(@PathVariable Long notificationId,
                                                   Authentication authentication) {
        Member member = getMember(authentication);
        notificationService.updateNotification(member, notificationId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/notifications/count")
    public ResponseEntity countMemberNotReadNotification(Authentication authentication) {
        Long memberId = (getMember(authentication)).getId();
        return ResponseEntity.ok(notificationService.countMemberNotReadNotifications(memberId));
    }

    private Member getMember(Authentication authentication) {
        return (Member) authentication.getPrincipal();
    }
}
