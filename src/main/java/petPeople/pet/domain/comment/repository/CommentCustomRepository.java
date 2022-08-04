package petPeople.pet.domain.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import petPeople.pet.controller.post.dto.resp.CommentRetrieveRespDto;
import petPeople.pet.domain.comment.entity.Comment;

import java.util.List;

public interface CommentCustomRepository {

    List<Comment> findAllByIdWithFetchJoinMemberPaging(Long postId);
    List<Comment> findByPostIds(List<Long> ids);

    List<Comment> findByPostId(Long postId);
    Long countByPostId(Long postId);

    void deleteCommentByPostId(Long postId);

    void deleteByCommentIds(List<Long> commentIds);
}
