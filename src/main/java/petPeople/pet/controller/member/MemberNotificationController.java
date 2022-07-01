package petPeople.pet.controller.member;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    @ApiOperation(value = "회원 알림 조회 API", notes = "알림 조회를 위해 header에 토큰을 입력해주세요")
    @GetMapping("/notifications")
    public ResponseEntity<Slice<MemberNotificationResponseDto>> getMemberNotifications(Pageable pageable,
                                                                                       Authentication authentication) {
        Long memberId = getMember(authentication).getId();
        return ResponseEntity.ok(notificationService.retrieveNotifications(memberId, pageable));
    }

    @ApiOperation(value = "회원 전체 알림 갱신 API", notes = "알림 갱신울 위해 header에 토큰을 입력해주세요")
    @PatchMapping("/notifications")
    public ResponseEntity updateMemberNotifications(Authentication authentication) {
        Long memberId = (getMember(authentication)).getId();
        notificationService.updateNotifications(memberId);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "회원 단건 알림 갱신 API", notes = "갱신할 알림의 notificationId를 경로변수에 넣어주세요. 알림 단건 갱신울 위해 header에 토큰을 입력해주세요")
    @PatchMapping("/notifications/{notificationId}")
    public ResponseEntity updateMemberNotification(Authentication authentication,
                                                   @ApiParam(value = "알림 ID", required = true) @PathVariable Long notificationId) {
        Member member = getMember(authentication);
        notificationService.updateNotification(member, notificationId);

        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "회원 알림 삭제 API", notes = "알림 삭제를 위해 header에 토큰을 입력해주세요")
    @DeleteMapping("/notifications")
    public ResponseEntity deleteAllNotification(Authentication authentication) {
        Long memberId = (getMember(authentication)).getId();
        notificationService.deleteAllNotification(memberId);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "회원 알림 개수 조회 API", notes = "알림 개수 조회를 위해 header에 토큰을 입력해주세요")
    @GetMapping("/notifications/count")
    public ResponseEntity countMemberNotReadNotification(Authentication authentication) {
        Long memberId = (getMember(authentication)).getId();
        return ResponseEntity.ok(notificationService.countMemberNotReadNotifications(memberId));
    }

    private Member getMember(Authentication authentication) {
        return (Member) authentication.getPrincipal();
    }
}
