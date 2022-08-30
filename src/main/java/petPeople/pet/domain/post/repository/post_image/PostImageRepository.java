package petPeople.pet.domain.post.repository.post_image;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.post.entity.PostImage;

public interface PostImageRepository extends JpaRepository<PostImage, Long>, PostImageCustomRepository {
}
