package petPeople.pet.domain.meeting.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import petPeople.pet.domain.meeting.entity.MeetingComment;

public interface MeetingCommentCustomRepository {
    Slice<MeetingComment> findByMeetingPostId(Pageable pageable, Long meetingPostId);
}
