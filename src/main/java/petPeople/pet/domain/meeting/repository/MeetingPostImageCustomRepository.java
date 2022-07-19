package petPeople.pet.domain.meeting.repository;

import org.springframework.lang.Nullable;
import petPeople.pet.domain.meeting.entity.MeetingPost;
import petPeople.pet.domain.meeting.entity.MeetingPostImage;

import java.util.List;

public interface MeetingPostImageCustomRepository {
    @Nullable
    List<MeetingPostImage> findAllMeetingPostImageByMeetingPostId(Long meetingPostId);
    List<MeetingPostImage> findAllByMeetingPostIds(List<Long> meetingPostIds);
    void deleteByMeetingPostId(Long meetingPostId);
    List<MeetingPostImage> findAllByMeetingId(Long meetingId);
}
