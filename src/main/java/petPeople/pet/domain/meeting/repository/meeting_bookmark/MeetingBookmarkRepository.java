package petPeople.pet.domain.meeting.repository.meeting_bookmark;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.meeting.entity.MeetingBookmark;

public interface MeetingBookmarkRepository extends JpaRepository<MeetingBookmark, Long>, MeetingBookmarkCustomRepository {

}
