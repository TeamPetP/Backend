package petPeople.pet.controller.member.dto.resp.notificationResp;

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
@Getter @Setter
public class NotificationCommentRetrieveResponseDto extends MemberNotificationResponseDto {

    private Long commentId;

    private Long postId;

    private String content;

    private List<String> postImgUrlList = new ArrayList<>();

    public NotificationCommentRetrieveResponseDto(Notification notification, List<PostImage> postImageList) {
        super(notification, "commentLike");
        this.commentId = notification.getComment().getId();
        this.postId = notification.getComment().getPost().getId();
        this.content = notification.getComment().getContent();

        for (PostImage postImage : postImageList) {
            this.postImgUrlList.add(postImage.getImgUrl());
        }
    }
}
