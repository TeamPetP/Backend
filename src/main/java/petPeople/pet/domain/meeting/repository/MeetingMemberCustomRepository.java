package petPeople.pet.domain.meeting.repository;

import petPeople.pet.domain.meeting.entity.MeetingMember;

import java.util.List;

public interface MeetingMemberCustomRepository {
    List<MeetingMember> findByMeetingIds(List<Long> meetingIds);
    List<MeetingMember> findByMeetingId(Long meetingId);
    Long countByMeetingId(Long meetingId);
    Long countByMemberId(Long memberId);
    void deleteByMeetingIdAndMemberId(Long meetingId, Long memberId);
}
