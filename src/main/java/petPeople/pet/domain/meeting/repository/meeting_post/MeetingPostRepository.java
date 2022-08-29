package petPeople.pet.domain.meeting.repository.meeting_post;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.meeting.entity.MeetingPost;

public interface MeetingPostRepository extends JpaRepository<MeetingPost, Long>, MeetingPostCustomRepository {
}
