package petPeople.pet.controller.meeting.dto.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

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

    @ApiModelProperty(required = true, value = "모임 게시글 댓글 좋아요 여부", example = "true/false")
    private Boolean isLiked;

    @ApiModelProperty(required = true, value = "모임 게시글 댓글 좋아요 수", example = "10")
    private Long likeCnt;

}
