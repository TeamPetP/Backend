package petPeople.pet.domain.meeting.repository.meeting;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.meeting.entity.Meeting;

public interface MeetingRepository extends JpaRepository<Meeting, Long>, MeetingCustomRepository {
}
