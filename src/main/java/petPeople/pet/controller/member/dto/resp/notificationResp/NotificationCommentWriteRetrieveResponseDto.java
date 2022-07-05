package petPeople.pet.controller.member.dto.resp.notificationResp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "회원의 게시글에 댓글 알림 응답 DTO")
public class NotificationCommentWriteRetrieveResponseDto extends MemberNotificationResponseDto {

    @ApiModelProperty(required = true, value = "댓글 ID", example = "1")
    private Long commentId;

    @ApiModelProperty(required = true, value = "게시글 ID", example = "2")
    private Long postId;

    @ApiModelProperty(required = true, value = "게시글 제목", example = "누가 내 머리에 똥 쌌냐?")
    private String postTitle;

    @ApiModelProperty(required = true, value = "게시글 내용", example = "누가 내 머리에 똥 쌌어?!")
    private String content;

    @ApiModelProperty(required = true, value = "게시글 이미지", example = "www.img.com")
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
