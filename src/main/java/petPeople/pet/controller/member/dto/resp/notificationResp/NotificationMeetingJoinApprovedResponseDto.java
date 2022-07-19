package petPeople.pet.controller.member.dto.resp.notificationResp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import petPeople.pet.domain.notification.entity.Notification;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ApiModel(description = "회원의 모임 가입 승인 알림 응답 DTO")
public class NotificationMeetingJoinApprovedResponseDto extends MemberNotificationResponseDto {

    @ApiModelProperty(required = true, value = "미팅 ID", example = "1")
    private Long meetingId;

    @ApiModelProperty(required = true, value = "미팅 제목", example = "토르 러브 엔 썬더 보고싶당 여긴 제목")
    private String meetingTitle;

    public NotificationMeetingJoinApprovedResponseDto(Notification notification) {
        super(notification, "meetingJoinApproved");
        this.meetingId = notification.getMeeting().getId();
        this.meetingTitle = notification.getMeeting().getTitle();
    }
}
