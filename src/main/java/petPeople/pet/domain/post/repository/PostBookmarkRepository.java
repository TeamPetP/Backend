package petPeople.pet.domain.post.repository;

import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.post.entity.PostBookmark;

import java.util.Optional;

public interface PostBookmarkRepository extends JpaRepository<PostBookmark, Long>, PostBookmarkCustomRepository{

}
