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
public class NotificationPostRetrieveResponseDto extends MemberNotificationResponseDto {

    private Long postId;

    private String content;

    private List<String> postImgUrlList = new ArrayList<>();

    public NotificationPostRetrieveResponseDto(Notification notification, List<PostImage> postImageList) {
        super(notification, "postLike");
        this.postId = notification.getPost().getId();
        this.content = notification.getPost().getContent();

        for (PostImage postImage : postImageList) {
            postImgUrlList.add(postImage.getImgUrl());
        }
    }
}
