package petPeople.pet.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import petPeople.pet.domain.post.entity.Tag;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByPostId(Long postId);

    @Modifying(clearAutomatically = true)
    @Query("delete from Tag t where t.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
