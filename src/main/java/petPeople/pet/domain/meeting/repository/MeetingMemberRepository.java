package petPeople.pet.domain.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.meeting.entity.MeetingCommentLike;
import petPeople.pet.domain.meeting.entity.MeetingMember;

import java.util.List;

public interface MeetingMemberRepository extends JpaRepository<MeetingMember, Long>, MeetingMemberCustomRepository {
}
