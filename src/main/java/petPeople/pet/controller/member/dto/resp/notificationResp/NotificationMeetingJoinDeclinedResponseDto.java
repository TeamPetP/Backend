package petPeople.pet.controller.member.dto.resp.notificationResp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import petPeople.pet.domain.notification.entity.Notification;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ApiModel(description = "회원의 모임 가입 거절 알림 응답 DTO")
public class NotificationMeetingJoinDeclinedResponseDto extends MemberNotificationResponseDto {

    @ApiModelProperty(required = true, value = "미팅 ID", example = "1")
    private Long meetingId;

    @ApiModelProperty(required = true, value = "미팅 제목", example = "토르 러브 엔 썬더 보고싶당 여긴 제목")
    private String meetingTitle;

    public NotificationMeetingJoinDeclinedResponseDto(Notification notification) {
        super(notification, "meetingJoinDeclined");
        this.meetingId = notification.getMeeting().getId();
        this.meetingTitle = notification.getMeeting().getTitle();
    }
}
