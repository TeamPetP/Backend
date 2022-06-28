package petPeople.pet.domain.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.meeting.entity.MeetingComment;
import petPeople.pet.domain.meeting.entity.MeetingCommentLike;

import java.util.List;

public interface MeetingCommentLikeRepository extends JpaRepository<MeetingCommentLike, Long>, MeetingCommentLikeCustomRepository {
}
