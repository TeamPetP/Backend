package petPeople.pet.domain.meeting.repository.meeting_member;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.meeting.entity.MeetingMember;

public interface MeetingMemberRepository extends JpaRepository<MeetingMember, Long>, MeetingMemberCustomRepository {
}
