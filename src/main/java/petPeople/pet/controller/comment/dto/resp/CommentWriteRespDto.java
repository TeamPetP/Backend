package petPeople.pet.controller.comment.dto.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import petPeople.pet.domain.comment.entity.Comment;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "댓글 작성 응답 DTO")
public class CommentWriteRespDto {

    @ApiModelProperty(required = true, value = "댓글 ID", example = "1")
    private Long commentId;

    @ApiModelProperty(required = true, value = "회원 ID", example = "2")
    private Long memberId;

    @ApiModelProperty(required = true, value = "게시글 ID", example = "2")
    private Long postId;

    @ApiModelProperty(required = true, value = "댓글 내용", example = "물고기 산책 시키러 가실 분!")
    private String content;

    @ApiModelProperty(required = true, value = "작성 시간", example = "2022-06-28T07:38:14.152321")
    private LocalDateTime createDate;

    @ApiModelProperty(required = false, value = "부모 댓글 존재 여부", example = "true")
    private boolean hasParent;

    @ApiModelProperty(required = false, value = "부모 댓글Id", example = "1")
    private Long parentId;

    public CommentWriteRespDto(Comment comment) {
        this.commentId = comment.getId();
        this.memberId = comment.getMember().getId();
        this.postId = comment.getPost().getId();
        this.content = comment.getContent();
        this.createDate = comment.getCreatedDate();
        if (comment.getParent() != null) {
            this.hasParent = true;
            this.parentId = comment.getParent().getId();
        }
    }
}
