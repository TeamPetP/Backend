package petPeople.pet.domain.post.repository.post_bookmark;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.post.entity.PostBookmark;

public interface PostBookmarkRepository extends JpaRepository<PostBookmark, Long>, PostBookmarkCustomRepository{

}
