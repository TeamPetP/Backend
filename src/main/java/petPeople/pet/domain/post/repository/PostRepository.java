package petPeople.pet.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.post.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
