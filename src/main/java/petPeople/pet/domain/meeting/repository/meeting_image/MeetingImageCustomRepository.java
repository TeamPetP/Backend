package petPeople.pet.domain.meeting.repository.meeting_image;

import petPeople.pet.domain.meeting.entity.MeetingImage;

import java.util.List;

public interface MeetingImageCustomRepository {
    List<MeetingImage> findByMeetingIds(List<Long> meetingIds);
    List<MeetingImage> findByMeetingId(Long meetingId);
    Long deleteByMeetingId(Long meetingId);
}
