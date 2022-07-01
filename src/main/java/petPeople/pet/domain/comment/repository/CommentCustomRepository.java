package petPeople.pet.domain.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import petPeople.pet.controller.post.dto.resp.CommentRetrieveRespDto;
import petPeople.pet.domain.comment.entity.Comment;

import java.util.List;

public interface CommentCustomRepository {

    Slice<Comment> findAllByIdWithFetchJoinMemberPaging(Long postId, Pageable pageable);
    List<Comment> findByPostIds(List<Long> ids);
    Long countByPostId(Long postId);
}
