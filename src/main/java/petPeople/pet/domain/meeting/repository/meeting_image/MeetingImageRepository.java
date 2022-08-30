package petPeople.pet.domain.meeting.repository.meeting_image;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.meeting.entity.MeetingImage;

public interface MeetingImageRepository extends JpaRepository<MeetingImage, Long>, MeetingImageCustomRepository {
}
