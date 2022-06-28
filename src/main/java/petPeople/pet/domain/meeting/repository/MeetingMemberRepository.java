package petPeople.pet.domain.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.meeting.entity.MeetingMember;

public interface MeetingMemberRepository extends JpaRepository<MeetingMember, Long>, MeetingMemberCustomRepository {
}
