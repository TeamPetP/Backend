package petPeople.pet.domain.meeting.repository;

import petPeople.pet.domain.meeting.entity.MeetingCommentLike;

import java.util.List;
import java.util.Optional;

public interface MeetingCommentLikeCustomRepository {
    List<MeetingCommentLike> findByMeetingCommentIds(List<Long> meetingCommentIds);
    Optional<MeetingCommentLike> findByMeetingPostIdAndMemberId(Long meetingCommentId, Long memberId);
    void deleteByMeetingCommentIdAndMemberId(Long meetingCommentId, Long memberId);
    Long countByMeetingCommentId(Long meetingCommentId);
}
