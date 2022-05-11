package petPeople.pet.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import petPeople.pet.domain.post.entity.PostImage;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long>, PostImageCustomRepository {
}
