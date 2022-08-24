package petPeople.pet.domain.comment.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {
}
