package petPeople.pet.domain.meeting.repository.meeting_post_Image;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.meeting.entity.MeetingPostImage;

public interface MeetingPostImageRepository extends JpaRepository<MeetingPostImage, Long>, MeetingPostImageCustomRepository {
}
