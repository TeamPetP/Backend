package petPeople.pet.domain.meeting.repository.custom;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import petPeople.pet.domain.meeting.entity.MeetingWaitingMember;

import java.util.List;
import java.util.Optional;

public interface MeetingWaitingMemberCustomRepository {
    List<MeetingWaitingMember> findAllByMeetingIdFetchJoinMember(Long meetingId);
    Optional<MeetingWaitingMember> findByMeetingIdAndMemberIdFetchJoinMember(Long meetingId, Long memberId);
    Slice<MeetingWaitingMember> findAllByMemberIdFetchJoinMemberAndMeeting(Pageable pageable, Long memberId);
}
