package petPeople.pet.domain.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.meeting.entity.MeetingComment;
import petPeople.pet.domain.meeting.entity.MeetingImage;

public interface MeetingCommentRepository extends JpaRepository<MeetingComment, Long> {
}
