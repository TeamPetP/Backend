package petPeople.pet.domain.post.repository.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.post.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long>,  TagCustomRepository {
}
