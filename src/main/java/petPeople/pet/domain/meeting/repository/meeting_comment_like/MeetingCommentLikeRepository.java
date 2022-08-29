package petPeople.pet.domain.meeting.repository.meeting_comment_like;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.meeting.entity.MeetingCommentLike;

public interface MeetingCommentLikeRepository extends JpaRepository<MeetingCommentLike, Long>, MeetingCommentLikeCustomRepository {
}
