package petPeople.pet.domain.comment.repository.commentLike;

import petPeople.pet.domain.comment.entity.CommentLike;

import java.util.List;
import java.util.Optional;

public interface CommentLikeCustomRepository {

    List<CommentLike> findCommentLikesByCommentIds(List<Long> ids);
    List<CommentLike> findCommentLikesByPostId(Long postId);
    Long countByCommentId(Long commentId);
    void deleteByCommentId(Long commentId);
    Optional<CommentLike> findCommentLikeByCommentIdAndMemberId(Long id, Long commentId);
    void deleteByCommentIdAndMemberId(Long id, Long commentId);
}
