package petPeople.pet.domain.meeting.repository;

import petPeople.pet.domain.meeting.entity.MeetingWaitingMember;

import java.util.List;

public interface MeetingWaitingMemberCustomRepository {
    List<MeetingWaitingMember> findAllByMeetingId(Long meetingId);
}
