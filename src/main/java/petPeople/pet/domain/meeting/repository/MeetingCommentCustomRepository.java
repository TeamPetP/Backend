package petPeople.pet.domain.meeting.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import petPeople.pet.domain.meeting.entity.MeetingComment;

import java.util.List;

public interface MeetingCommentCustomRepository {
    List<MeetingComment> findByMeetingPostId(Long meetingPostId);

    Long countByMeetingPostId(Long meetingPostId);

    void deleteMeetingCommentByMeetingPostId(Long meetingPostId);
}
