package petPeople.pet.controller.comment.dto.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import petPeople.pet.domain.comment.entity.Comment;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel(description = "댓글 수정 응답 DTO")
@EqualsAndHashCode
public class CommentEditRespDto {

    @ApiModelProperty(required = true, value = "댓글 ID", example = "1")
    private Long commentId;

    @ApiModelProperty(required = true, value = "회원 ID", example = "2")
    private Long memberId;

    @ApiModelProperty(required = true, value = "게시글 ID", example = "2")
    private Long postId;

    @ApiModelProperty(required = true, value = "댓글 내용", example = "물고기 산책 시키러 가실 분!")
    private String content;

    @ApiModelProperty(required = true, value = "댓글 좋아요 개수", example = "3")
    private Long likeCnt;

    @ApiModelProperty(required = true, value = "작성 시간", example = "2022-06-28T07:38:14.152321")
    private LocalDateTime createdDate;

    @ApiModelProperty(required = true, value = "회원 닉네임", example = "방울아빠")
    private String nickName;

    @ApiModelProperty(required = true, value = "회원 이미지", example = "www.img.url")
    private String memberImageUrl;

    public CommentEditRespDto(Comment comment, Long likeCnt) {
        this.content = comment.getContent();
        this.commentId = comment.getId();
        this.postId = comment.getPost().getId();
        this.memberId = comment.getMember().getId();
        this.createdDate = comment.getCreatedDate();
        this.nickName = comment.getMember().getNickname();
        this.memberImageUrl = comment.getMember().getImgUrl();
        this.likeCnt = likeCnt;
    }
}
