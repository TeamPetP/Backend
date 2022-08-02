package petPeople.pet.controller.meeting.dto.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import petPeople.pet.domain.meeting.entity.MeetingPost;
import petPeople.pet.domain.meeting.entity.MeetingPostImage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
@ApiModel(description = "모임 게시글 조회 응답 DTO")
public class MeetingPostRetrieveRespDto {

    @ApiModelProperty(required = true, value = "모임 게시글 ID", example = "1")
    private Long meetingPostId;

    @ApiModelProperty(required = true, value = "모임 ID", example = "1")
    private Long meetingId;

    @ApiModelProperty(required = true, value = "회원 ID", example = "1")
    private Long memberId;

    @ApiModelProperty(required = true, value = "회원 이미지 url", example = "www.img.com")
    private String memberImgUrl;

    @ApiModelProperty(required = true, value = "회원 닉네임", example = "abcd")
    private String nickname;

    @ApiModelProperty(required = true, value = "모임 게시글 제목", example = "안녕하세요")
    private String title;

    @ApiModelProperty(required = true, value = "모임 게시글 내용", example = "감사합니다~")
    private String content;

    @ApiModelProperty(required = true, value = "이미지 url 리스트", example = "www.img.com")
    private List<String> imgUrlList = new ArrayList<>();

    @ApiModelProperty(required = true, value = "작성 시간", example = "2022-06-28T07:38:14.152321")
    private LocalDateTime createdDate;

    @ApiModelProperty(required = true, value = "좋아요 수", example = "3")
    private Long likeCnt;

    @ApiModelProperty(required = true, value = "좋아여 요부", example = "true/false")
    private Boolean isLiked;

    @ApiModelProperty(required = true, value = "자신의 게시글인지", example = "true/false")
    private Boolean owner;

    public MeetingPostRetrieveRespDto(MeetingPost meetingPost, List<MeetingPostImage> meetingPostImageList, Long likeCnt, boolean isLiked) {
        this.meetingPostId = meetingPost.getId();
        this.meetingId = meetingPost.getMeeting().getId();
        this.memberId = meetingPost.getMember().getId();
        this.memberImgUrl = meetingPost.getMember().getImgUrl();
        this.nickname = meetingPost.getMember().getNickname();
        this.title = meetingPost.getTitle();
        this.content = meetingPost.getContent();
        this.createdDate = meetingPost.getCreatedDate();

        for (MeetingPostImage meetingPostImage : meetingPostImageList) {
            imgUrlList.add(meetingPostImage.getImgUrl());
        }

        this.likeCnt = likeCnt;
        this.isLiked = isLiked;
    }

    public MeetingPostRetrieveRespDto(MeetingPost meetingPost, List<MeetingPostImage> meetingPostImageList, Long likeCnt, boolean isLiked, boolean isOwner) {
        this.meetingPostId = meetingPost.getId();
        this.meetingId = meetingPost.getMeeting().getId();
        this.memberId = meetingPost.getMember().getId();
        this.memberImgUrl = meetingPost.getMember().getImgUrl();
        this.nickname = meetingPost.getMember().getNickname();
        this.title = meetingPost.getTitle();
        this.content = meetingPost.getContent();
        this.createdDate = meetingPost.getCreatedDate();

        for (MeetingPostImage meetingPostImage : meetingPostImageList) {
            imgUrlList.add(meetingPostImage.getImgUrl());
        }

        this.likeCnt = likeCnt;
        this.isLiked = isLiked;
        this.owner = isOwner;
    }
}
