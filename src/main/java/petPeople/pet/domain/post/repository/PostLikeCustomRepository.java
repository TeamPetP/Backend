package petPeople.pet.domain.post.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import petPeople.pet.domain.post.entity.PostLike;

import java.util.List;
import java.util.Optional;

public interface PostLikeCustomRepository {

    List<PostLike> findPostLikesByPostIds(List<Long> ids);
    Optional<PostLike> findPostLikeByPostIdAndMemberId(Long postId, Long memberId);
    void deleteByPostId(Long postId);
    void deleteByPostIdAndMemberId(Long postId, Long memberId);
    Long countByPostId(Long postId);
    Slice<PostLike> findByMemberIdWithFetchJoinPost(Long memberId, Pageable pageable);
}
