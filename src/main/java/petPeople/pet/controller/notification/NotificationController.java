package petPeople.pet.controller.notification;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    @ApiOperation(value = "회원 알림 단건 삭제 API", notes = "삭제할 알림의 notificationId를 경로변수에 넣어주세요. 알림 조회를 위해 header에 토큰을 입력해주세요")
    @DeleteMapping("{notificationId}")
    public ResponseEntity deleteNotification(@ApiParam(value = "알림 ID", required = true) @PathVariable Long notificationId,
                                             Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        notificationService.deleteNotification(notificationId, member);
        return ResponseEntity.noContent().build();
    }
}
