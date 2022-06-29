package petPeople.pet.controller.member.dto.resp;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import petPeople.pet.domain.notification.entity.Notification;
import petPeople.pet.domain.post.entity.PostImage;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationMeetingCommentWriteRetrieveResponseDto extends MemberNotificationResponseDto {

    private Long meetingCommentId;

    private Long meetingPostId;

    private String postTitle;

    private String content;

    private List<String> postImgUrlList = new ArrayList<>();

    public NotificationMeetingCommentWriteRetrieveResponseDto(Notification notification) {
        super(notification, "MeetingCommentWrite");
        this.meetingCommentId = notification.getMeetingComment().getId();
        this.content = notification.getWriteComment().getContent();
        this.meetingPostId = notification.getMeetingPost().getId();
    }
}
