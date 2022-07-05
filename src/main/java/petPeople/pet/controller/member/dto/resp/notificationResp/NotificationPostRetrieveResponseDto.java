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
@ApiModel(description = "회원의 게시글에 좋아요 알림 응답 DTO")
public class NotificationPostRetrieveResponseDto extends MemberNotificationResponseDto {

    @ApiModelProperty(required = true, value = "게시글 ID", example = "1")
    private Long postId;

    @ApiModelProperty(required = true, value = "게시글 내용", example = "7월 6일 대개봉박두사부일체 여긴 내용")
    private String content;

    @ApiModelProperty(required = true, value = "게시글 이미지", example = "www.img.com")
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
