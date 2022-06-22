package petPeople.pet.domain.meeting.repository;

import petPeople.pet.domain.meeting.entity.MeetingCommentLike;

import java.util.List;

public interface MeetingCommentLikeCustomRepository {
    List<MeetingCommentLike> findByMeetingPostId(Long meetingPostId);
}
