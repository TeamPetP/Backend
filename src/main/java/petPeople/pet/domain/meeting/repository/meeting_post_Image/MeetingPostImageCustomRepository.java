package petPeople.pet.domain.meeting.repository.meeting_post_Image;

import org.springframework.lang.Nullable;
import petPeople.pet.domain.meeting.entity.MeetingPostImage;

import java.util.List;

public interface MeetingPostImageCustomRepository {
    List<MeetingPostImage> findAllMeetingPostImageByMeetingPostId(Long meetingPostId);
    List<MeetingPostImage> findAllByMeetingPostIds(List<Long> meetingPostIds);
    Long deleteByMeetingPostId(Long meetingPostId);
    List<MeetingPostImage> findAllByMeetingId(Long meetingId);
}
