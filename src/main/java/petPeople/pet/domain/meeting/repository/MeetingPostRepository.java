package petPeople.pet.domain.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.meeting.entity.MeetingPost;
import petPeople.pet.domain.meeting.repository.custom.MeetingPostCustomRepository;

public interface MeetingPostRepository extends JpaRepository<MeetingPost, Long>, MeetingPostCustomRepository {
}
