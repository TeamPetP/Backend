package petPeople.pet.domain.meeting.repository;

import petPeople.pet.domain.meeting.entity.MeetingImage;

import java.util.List;

public interface MeetingImageCustomRepository {
    List<MeetingImage> findByMeetingIds(List<Long> meetingIds);
    List<MeetingImage> findByMeetingId(Long meetingId);
    void deleteByMeetingId(Long meetingId);
}
