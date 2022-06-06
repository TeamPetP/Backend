package petPeople.pet.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.comment.entity.CommentLike;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long>, CommentLikeCustomRepository{

}
