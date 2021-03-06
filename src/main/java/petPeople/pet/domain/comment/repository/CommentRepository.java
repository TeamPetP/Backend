package petPeople.pet.domain.comment.repository;

import com.querydsl.core.group.GroupBy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.controller.post.dto.resp.CommentRetrieveRespDto;
import petPeople.pet.domain.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {
}
