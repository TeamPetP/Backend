package petPeople.pet.domain.meeting.repository.meeting_comment;

import petPeople.pet.domain.meeting.entity.MeetingComment;

import java.util.List;

public interface MeetingCommentCustomRepository {
    List<MeetingComment> findByMeetingPostId(Long meetingPostId);

    Long countByMeetingPostId(Long meetingPostId);

    void deleteMeetingCommentById(Long meetingCommentId);
    void deleteMeetingCommentByMeetingPostId(Long meetingPostId);
    void deleteMeetingCommentByIds(List<Long> meetingPostIds);
}
