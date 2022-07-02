package petPeople.pet.controller.member.dto.resp.notificationResp;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "회원의 모임 댓글에 좋아요 알림 응답 DTO")
public class NotificationMeetingCommentRetrieveResponseDto extends MemberNotificationResponseDto {

    @ApiModelProperty(required = true, value = "모임 댓글 ID", example = "1")
    private Long meetingCommentId;

    @ApiModelProperty(required = true, value = "모임 게시글 ID", example = "2")
    private Long meetingPostId;

    @ApiModelProperty(required = true, value = "모임 게시글 제목", example = "토르 러브 엔 썬더 보고싶당 여긴 제목")
    private String postTitle;

    @ApiModelProperty(required = true, value = "모임 댓글 내용", example = "7월 6일 대개봉박두사부일체 여긴 내용")
    private String content;

    @ApiModelProperty(required = true, value = "게시글 이미지", example = "www.img.com")
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
