package petPeople.pet.controller.post.dto.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import petPeople.pet.domain.comment.entity.Comment;
import petPeople.pet.domain.comment.entity.CommentLike;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "댓글 작성 응답 DTO")
public class CommentRetrieveRespDto {

    @ApiModelProperty(required = true, value = "댓글 ID", example = "3")
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
    private List<CommentRetrieveRespDto> childComment = new ArrayList<>();

    public CommentRetrieveRespDto(Comment comment, Long likeCnt, Boolean isLiked, List<CommentLike> findCommentLikeList, Long memberId) {
        this.content = comment.getContent();
        this.commentId = comment.getId();
        this.postId = comment.getPost().getId();
        this.memberId = comment.getMember().getId();
        this.createdDate = comment.getCreatedDate();
        this.nickName = comment.getMember().getNickname();
        this.memberImageUrl = comment.getMember().getImgUrl();
        this.likeCnt = likeCnt;
        this.isLiked = isLiked;
        if (comment.getChild() != null) {
            for (Comment c : comment.getChild()) {
                Long likeCount = 0L;
                boolean flag = false;
                for (CommentLike commentLike : findCommentLikeList) {
                    likeCount = countCommentLike(c, likeCount, commentLike);
                    flag = checkLike(memberId, flag, commentLike);
                }
                this.childComment.add(new CommentRetrieveRespDto(c, likeCount, flag, findCommentLikeList, memberId));
            }
        }
    }

    private Long countCommentLike(Comment c, Long likeCount, CommentLike commentLike) {
        if (c.getId().equals(commentLike.getComment().getId())) {
            likeCount++;
        }
        return likeCount;
    }

    private boolean checkLike(Long memberId, boolean flag, CommentLike commentLike) {
        if (commentLike.getMember().getId().equals(memberId)) {
            flag = true;
        }
        return flag;
    }
}
