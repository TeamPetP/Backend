package petPeople.pet.domain.meeting.repository.meeting_waiting_member;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.meeting.entity.MeetingWaitingMember;

public interface MeetingWaitingMemberRepository extends JpaRepository<MeetingWaitingMember, Long>, MeetingWaitingMemberCustomRepository {
}
