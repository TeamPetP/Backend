package petPeople.pet.domain.post.repository.post_image;

import org.springframework.lang.Nullable;
import petPeople.pet.domain.post.entity.PostImage;

import java.util.List;

public interface PostImageCustomRepository {

    void deleteByPostId(Long postId);
    List<PostImage> findPostImagesByPostIds(List<Long> postIds);
    @Nullable
    List<PostImage> findByPostId(Long postId);
}
