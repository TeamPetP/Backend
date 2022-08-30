package petPeople.pet.domain.post.repository.post_like;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.post.entity.PostLike;

public interface PostLikeRepository extends JpaRepository<PostLike, Long>, PostLikeCustomRepository {
}
