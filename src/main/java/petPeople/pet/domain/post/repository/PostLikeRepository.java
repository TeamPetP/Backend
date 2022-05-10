package petPeople.pet.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.post.entity.PostLike;

import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLike, Long>, PostLikeCustomRepository {

    Long countByPostId(Long postId);

}
