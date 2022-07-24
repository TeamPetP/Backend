package petPeople.pet.controller.member.dto.resp.notificationResp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import petPeople.pet.domain.notification.entity.Notification;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
@ApiModel(description = "회원의 알림 기본 응답 DTO")
public class MemberNotificationResponseDto {

    @ApiModelProperty(required = true, value = "알림 ID", example = "1")
    private Long notificationId;

    @ApiModelProperty(required = true, value = "회원 ID", example = "1")
    private Long memberId;

    @ApiModelProperty(required = true, value = "회원 이미지 url", example = "www.img.com")
    private String memberImgUrl;

    @ApiModelProperty(required = true, value = "회원 별명", example = "아스방가르드")
    private String nickname;

    @ApiModelProperty(required = true, value = "알림 확인 여부", example = "true")
    private boolean isChecked;

    @ApiModelProperty(required = true, value = "작성 시간", example = "2022-06-28T07:38:14.152321")
    private LocalDateTime createdDate;

    @ApiModelProperty(required = true, value = "알림 구분", example = "commentLike")
    private String notificationType;


    public MemberNotificationResponseDto(Notification notification, String notificationType) {
        this.notificationId = notification.getId();
        this.memberId = notification.getMember().getId();
        this.nickname = notification.getMember().getNickname();
        this.isChecked = notification.isChecked();
        this.createdDate = notification.getCreatedDate();
        this.notificationType = notificationType;
        this.memberImgUrl = notification.getMember().getImgUrl();
    }
}
