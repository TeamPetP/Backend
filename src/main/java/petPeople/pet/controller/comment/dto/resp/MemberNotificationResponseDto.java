package petPeople.pet.controller.comment.dto.resp;

import lombok.*;
import petPeople.pet.domain.notification.entity.Notification;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberNotificationResponseDto {

    private Long notificationId;

    private Long memberId;

    private String nickname;

    private boolean isChecked;

    private LocalDateTime createdDate;

    private String notificationType;

    public MemberNotificationResponseDto(Notification notification, String notificationType) {
        this.notificationId = notification.getId();
        this.memberId = notification.getMember().getId();
        this.nickname = notification.getMember().getNickname();
        this.isChecked = notification.isChecked();
        this.createdDate = notification.getCreatedDate();
        this.notificationType = notificationType;
    }
}
