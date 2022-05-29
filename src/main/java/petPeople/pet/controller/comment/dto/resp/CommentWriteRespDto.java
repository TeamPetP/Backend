package petPeople.pet.controller.comment.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import petPeople.pet.domain.comment.entity.Comment;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentWriteRespDto {

    private Long commentId;

    private Long memberId;

    private Long postId;

    private String content;

    private LocalDateTime createDate;

    public CommentWriteRespDto(Comment comment) {
        this.commentId = comment.getId();
        this.memberId = comment.getMember().getId();
        this.postId = comment.getPost().getId();
        this.content = comment.getContent();
        this.createDate = comment.getCreatedDate();
    }
}
