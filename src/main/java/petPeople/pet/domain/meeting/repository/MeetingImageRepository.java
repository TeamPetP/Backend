package petPeople.pet.domain.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.meeting.entity.MeetingImage;
import petPeople.pet.domain.meeting.repository.custom.MeetingImageCustomRepository;

public interface MeetingImageRepository extends JpaRepository<MeetingImage, Long>, MeetingImageCustomRepository {
}
