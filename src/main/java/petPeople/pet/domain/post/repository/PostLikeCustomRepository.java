package petPeople.pet.domain.post.repository;

import petPeople.pet.domain.post.entity.PostLike;

import java.util.List;
import java.util.Optional;

public interface PostLikeCustomRepository {

    List<PostLike> findPostLikesByPostIds(List<Long> ids);
    Optional<PostLike> findPostLikeByPostIdAndMemberId(Long postId, Long memberId);
}
