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
public class NotificationMeetingPostRetrieveResponseDto extends MemberNotificationResponseDto {

    private Long meetingCommentId;

    private Long meetingPostId;

    private String postTitle;

    private String content;

    private List<String> postImgUrlList = new ArrayList<>();

    public NotificationMeetingPostRetrieveResponseDto(Notification notification, List<MeetingPostImage> meetingPostImages) {
        super(notification, "meetingPostLike");
        this.meetingPostId = notification.getMeetingPost().getId();
        this.content = notification.getMeetingPost().getContent();
        this.postTitle = notification.getMeetingPost().getTitle();
        this.meetingPostId = notification.getMeetingPost().getId();

        for (MeetingPostImage meetingPostImage : meetingPostImages) {
            this.postImgUrlList.add(meetingPostImage.getImgUrl());
        }
    }
}
