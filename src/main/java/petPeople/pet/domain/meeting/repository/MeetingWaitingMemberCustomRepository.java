package petPeople.pet.domain.meeting.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import petPeople.pet.domain.meeting.entity.MeetingWaitingMember;

import java.util.List;
import java.util.Optional;

public interface MeetingWaitingMemberCustomRepository {
    List<MeetingWaitingMember> findAllByMeetingIdFetchJoinMember(Long meetingId);
    Optional<MeetingWaitingMember> findAllByMeetingIdAndMemberId(Long meetingId, Long memberId);
    Optional<MeetingWaitingMember> findByMeetingIdAndMemberIdFetchJoinMember(Long meetingId, Long memberId);
    Slice<MeetingWaitingMember> findAllByMemberIdFetchJoinMemberAndMeeting(Pageable pageable, Long memberId);
    void deleteByMeetingIdAndMemberId(Long meetingId, Long memberId);
    Optional<MeetingWaitingMember> findByMemberIdAndMeetingId(Long memberId, Long meetingId);
}
