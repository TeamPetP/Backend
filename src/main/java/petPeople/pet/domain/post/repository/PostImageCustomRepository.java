package petPeople.pet.domain.post.repository;

import petPeople.pet.domain.post.entity.PostImage;

import java.util.List;

public interface PostImageCustomRepository {

    void deleteByPostId(Long postId);
    List<PostImage> findPostImagesByPostIds(List<Long> postIds);
    List<PostImage> findByPostId(Long postId);
}
