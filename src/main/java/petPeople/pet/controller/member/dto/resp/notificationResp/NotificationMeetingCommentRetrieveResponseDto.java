package petPeople.pet.controller.member.dto.resp.notificationResp;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import petPeople.pet.domain.meeting.entity.MeetingPostImage;
import petPeople.pet.domain.notification.entity.Notification;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationMeetingCommentRetrieveResponseDto extends MemberNotificationResponseDto {

    private Long meetingCommentId;

    private Long meetingPostId;

    private String postTitle;

    private String content;

    private List<String> postImgUrlList = new ArrayList<>();

    public NotificationMeetingCommentRetrieveResponseDto(Notification notification, List<MeetingPostImage> meetingPostImages) {
        super(notification, "MeetingCommentLike");
        this.meetingCommentId = notification.getWriteMeetingComment().getId();
        this.content = notification.getWriteMeetingComment().getContent();
        this.meetingPostId = notification.getMeetingPost().getId();
        this.postTitle = notification.getMeetingPost().getTitle();

        for (MeetingPostImage meetingPostImage : meetingPostImages) {
            this.postImgUrlList.add(meetingPostImage.getImgUrl());
        }
    }
}
