package petPeople.pet.domain.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.meeting.entity.MeetingWaitingMember;
import petPeople.pet.domain.meeting.repository.custom.MeetingWaitingMemberCustomRepository;

public interface MeetingWaitingMemberRepository extends JpaRepository<MeetingWaitingMember, Long>, MeetingWaitingMemberCustomRepository {
}
