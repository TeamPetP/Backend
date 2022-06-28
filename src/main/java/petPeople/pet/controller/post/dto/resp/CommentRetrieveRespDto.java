package petPeople.pet.controller.post.dto.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import petPeople.pet.domain.comment.entity.Comment;

import java.time.LocalDateTime;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "댓글 작성 응답 DTO")
public class CommentRetrieveRespDto {

    private Long commentId;

    @ApiModelProperty(required = true, value = "회원 ID", example = "2")
    private Long memberId;

    @ApiModelProperty(required = true, value = "게시글 ID", example = "1")
    private Long postId;

    @ApiModelProperty(required = true, value = "게시글 내용", example = "강아지 좋아")
    private String content;

    @ApiModelProperty(required = true, value = "좋아요 수", example = "3")
    private Long likeCnt;

    @ApiModelProperty(required = true, value = "작성 시간", example = "2022-06-28T07:38:14.152321")
    private LocalDateTime createdDate;

    @ApiModelProperty(required = true, value = "회원 닉네임", example = "abcd")
    private String nickName;

    @ApiModelProperty(required = true, value = "회원 이미지", example = "www.img.url")
    private String memberImageUrl;

    @ApiModelProperty(required = true, value = "좋아요 여부", example = "true/false")
    private Boolean isLiked;

    public CommentRetrieveRespDto(Comment comment, Long likeCnt, Boolean isLiked) {
        this.content = comment.getContent();
        this.commentId = comment.getId();
        this.postId = comment.getPost().getId();
        this.memberId = comment.getMember().getId();
        this.createdDate = comment.getCreatedDate();
        this.nickName = comment.getMember().getNickname();
        this.memberImageUrl = comment.getMember().getImgUrl();
        this.likeCnt = likeCnt;
        this.isLiked = isLiked;
    }
}
