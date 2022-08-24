package petPeople.pet.domain.meeting.repository.meeting_post_like;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.meeting.entity.MeetingPostLike;

public interface MeetingPostLikeRepository extends JpaRepository<MeetingPostLike, Long>, MeetingPostLikeCustomRepository {
}
