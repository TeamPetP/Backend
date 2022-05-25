package petPeople.pet.domain.meeting.repository;

import petPeople.pet.domain.meeting.entity.MeetingWaitingMember;

import java.util.List;
import java.util.Optional;

public interface MeetingWaitingMemberCustomRepository {
    List<MeetingWaitingMember> findAllByMeetingId(Long meetingId);
    Optional<MeetingWaitingMember> findByMeetingIdAndMemberId(Long meetingId, Long memberId);
}
