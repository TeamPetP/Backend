package petPeople.pet.domain.meeting.repository;

import petPeople.pet.domain.meeting.entity.MeetingImage;
import petPeople.pet.domain.meeting.entity.MeetingImageFile;

import java.util.List;

public interface MeetingImageFileCustomRepository {
    List<MeetingImageFile> findByMeetingIds(List<Long> meetingIds);
    List<MeetingImageFile> findByMeetingId(Long meetingId);
    void deleteByMeetingId(Long meetingId);
}
