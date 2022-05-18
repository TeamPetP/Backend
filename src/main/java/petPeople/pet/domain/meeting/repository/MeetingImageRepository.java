package petPeople.pet.domain.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.meeting.entity.Meeting;
import petPeople.pet.domain.meeting.entity.MeetingImage;

import java.util.List;
import java.util.Optional;

public interface MeetingImageRepository extends JpaRepository<MeetingImage, Long> {
    List<MeetingImage> findByMeetingId(Long meetingId);
}
