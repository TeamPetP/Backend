package petPeople.pet.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import petPeople.pet.domain.post.entity.Post;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p join fetch p.member m where p.id = :postId")
    Optional<Post> findByIdWithFetchJoinMember(@Param("postId") Long postId);
}
