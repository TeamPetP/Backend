package petPeople.pet.domain.meeting.repository.meeting_member;

import petPeople.pet.domain.meeting.entity.MeetingMember;

import java.util.List;

public interface MeetingMemberCustomRepository {
    List<MeetingMember> findByMeetingIds(List<Long> meetingIds);
    List<MeetingMember> findByMeetingId(Long meetingId);
    Long countByMeetingId(Long meetingId);
    Long countByMemberId(Long memberId);
    Long deleteByMeetingIdAndMemberId(Long meetingId, Long memberId);
}
