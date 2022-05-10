package petPeople.pet.domain.post.repository;

import org.springframework.data.repository.query.Param;
import petPeople.pet.domain.post.entity.PostImage;

import java.util.List;

public interface PostImageCustomRepository {

    void deleteByPostId(Long postId);

    List<PostImage> findPostImagesByPostIds(List<Long> postIds);
}
