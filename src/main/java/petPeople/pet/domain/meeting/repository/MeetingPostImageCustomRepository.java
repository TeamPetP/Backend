package petPeople.pet.domain.meeting.repository;

import petPeople.pet.domain.meeting.entity.MeetingPost;
import petPeople.pet.domain.meeting.entity.MeetingPostImage;

import java.util.List;

public interface MeetingPostImageCustomRepository {
    List<MeetingPostImage> findAllMeetingPostImageByMeetingPostId(Long meetingPostId);
    List<MeetingPostImage> findAllByMeetingPostIds(List<Long> meetingPostIds);
}
