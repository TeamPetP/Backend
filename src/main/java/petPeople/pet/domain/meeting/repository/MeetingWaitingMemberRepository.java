package petPeople.pet.domain.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.meeting.entity.MeetingWaitingMember;

public interface MeetingWaitingMemberRepository extends JpaRepository<MeetingWaitingMember, Long>, MeetingWaitingMemberCustomRepository {
}
