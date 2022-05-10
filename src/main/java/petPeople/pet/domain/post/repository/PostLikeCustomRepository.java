package petPeople.pet.domain.post.repository;

import petPeople.pet.domain.post.entity.PostLike;

import java.util.List;

public interface PostLikeCustomRepository {

    List<PostLike> findPostLikesByPostIds(List<Long> ids);

}
