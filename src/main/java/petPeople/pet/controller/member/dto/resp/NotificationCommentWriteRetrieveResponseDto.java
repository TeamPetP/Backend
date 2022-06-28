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
@Getter @Setter
public class NotificationCommentWriteRetrieveResponseDto extends MemberNotificationResponseDto {

    private Long commentId;

    private Long postId;

    private String postTitle;

    private String content;

    private List<String> postImgUrlList = new ArrayList<>();

    public NotificationCommentWriteRetrieveResponseDto(Notification notification, List<PostImage> postImageList) {
        super(notification, "commentWrite");
        this.commentId = notification.getWriteComment().getId();
        this.content = notification.getWriteComment().getContent();
        this.postId = notification.getPost().getId();
        for (PostImage postImage : postImageList) {
            this.postImgUrlList.add(postImage.getImgUrl());
        }
    }
}
