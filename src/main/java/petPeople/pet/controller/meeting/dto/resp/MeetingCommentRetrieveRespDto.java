package petPeople.pet.controller.meeting.dto.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import petPeople.pet.controller.post.dto.resp.CommentRetrieveRespDto;
import petPeople.pet.domain.meeting.entity.MeetingComment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "모임 게시글 댓글 조회 응답 DTO")
public class MeetingCommentRetrieveRespDto {

    @ApiModelProperty(required = true, value = "모임 게시글 댓글 ID", example = "4")
    private Long meetingCommentId;

    @ApiModelProperty(required = true, value = "모임 게시글 ID", example = "3")
    private Long meetingPostId;

    @ApiModelProperty(required = true, value = "모임 ID", example = "2")
    private Long meetingId;

    @ApiModelProperty(required = true, value = "모임 게시글 내용", example = "강아지 산책하러 가요")
    private String content;

    @ApiModelProperty(required = true, value = "작성 시간", example = "2022-06-28T07:38:14.152321")
    private LocalDateTime createdDate;

    @ApiModelProperty(required = true, value = "회원 닉네임", example = "abcd")
    private String nickName;

    @ApiModelProperty(required = true, value = "회원 이미지", example = "www.img.url")
    private String memberImageUrl;

    @ApiModelProperty(required = false, value = "대댓글 조회", example = "[\n" +
            "                {\n" +
            "                    \"commentId\": 11,\n" +
            "                    \"memberId\": 3,\n" +
            "                    \"postId\": 4,\n" +
            "                    \"content\": \"사랑으로 키울게\",\n" +
            "                    \"likeCnt\": 2,\n" +
            "                    \"createdDate\": \"2022-07-13T08:30:54.86814\",\n" +
            "                    \"nickName\": \"방울이엄마\",\n" +
            "                    \"memberImageUrl\": \"htttestp:www.balladang.com\",\n" +
            "                    \"isLiked\": false,\n" +
            "                    \"childComment\": []\n" +
            "                }\n" +
            "            ]")
    private List<MeetingCommentRetrieveRespDto> childComment = new ArrayList<>();

    public MeetingCommentRetrieveRespDto(MeetingComment meetingComment, Long meetingPostId, Long meetingId) {
        this.meetingCommentId = meetingComment.getId();
        this.meetingPostId = meetingPostId;
        this.meetingId = meetingId;
        this.content = meetingComment.getContent();
        this.createdDate = meetingComment.getCreatedDate();
        this.nickName = meetingComment.getMember().getNickname();
        this.memberImageUrl = meetingComment.getMember().getImgUrl();

        if (meetingComment.getMeetingCommentChild() != null) {
            for (MeetingComment mc : meetingComment.getMeetingCommentChild()) {
                this.childComment.add(new MeetingCommentRetrieveRespDto(mc, meetingPostId, meetingId));
            }
        }
    }
}
