package petPeople.pet.domain.post.repository.tag;

import org.springframework.data.repository.query.Param;
import petPeople.pet.domain.post.entity.Tag;

import java.util.List;

public interface TagCustomRepository {

    Long deleteByPostId(Long postId);
    List<Tag> findTagsByPostIds(List<Long> postIds);
    List<Tag> findByPostId(Long postId);
}
