package petPeople.pet.controller.post.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import petPeople.pet.domain.comment.entity.Comment;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRetrieveRespDto {

    private Long commentId;

    private Long memberId;

    private Long postId;

    private String content;

    private Long likeCnt;

    private LocalDateTime createdDate;

    private String nickName;

    private String memberImageUrl;

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
