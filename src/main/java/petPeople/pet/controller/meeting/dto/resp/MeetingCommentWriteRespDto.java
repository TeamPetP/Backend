package petPeople.pet.controller.meeting.dto.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import petPeople.pet.domain.comment.entity.Comment;
import petPeople.pet.domain.meeting.entity.MeetingComment;

import java.time.LocalDateTime;

@EqualsAndHashCode
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "모임 게시글 댓글 작성 응답 DTO")
public class MeetingCommentWriteRespDto {

    @ApiModelProperty(required = true, value = "모임 게시글 댓글 ID", example = "4")
    private Long meetingCommentId;

    @ApiModelProperty(required = true, value = "모임 게시글 ID", example = "3")
    private Long meetingPostId;

    @ApiModelProperty(required = true, value = "회원 ID", example = "2")
    private Long memberId;

    @ApiModelProperty(required = true, value = "모임 ID", example = "2")
    private Long meetingId;

    @ApiModelProperty(required = true, value = "모임 게시글 내용", example = "강아지 산책하러 가요")
    private String content;

    @ApiModelProperty(required = true, value = "작성 시간", example = "2022-06-28T07:38:14.152321")
    private LocalDateTime createDate;

    @ApiModelProperty(required = false, value = "부모 모임 댓글 존재 여부", example = "true")
    private boolean hasParent;

    @ApiModelProperty(required = false, value = "부모 모임 댓글ID", example = "1")
    private Long parentId;

    public MeetingCommentWriteRespDto(MeetingComment meetingComment) {
        this.meetingCommentId = meetingComment.getId();
        this.memberId = meetingComment.getMember().getId();
        this.meetingPostId = meetingComment.getMeetingPost().getId();
        this.content = meetingComment.getContent();
        this.createDate = meetingComment.getCreatedDate();
        if (meetingComment.getMeetingCommentParent() != null) {
            this.hasParent = true;
            this.parentId = meetingComment.getMeetingCommentParent().getId();
        }
    }
}
