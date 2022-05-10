package petPeople.pet.domain.post.repository;

import org.springframework.data.repository.query.Param;
import petPeople.pet.domain.post.entity.Tag;

import java.util.List;

public interface TagCustomRepository {

    void deleteByPostId(Long postId);

    List<Tag> findTagsByPostIds(List<Long> postIds);

}
