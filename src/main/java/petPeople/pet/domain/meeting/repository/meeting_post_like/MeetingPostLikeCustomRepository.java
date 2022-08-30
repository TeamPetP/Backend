package petPeople.pet.domain.meeting.repository.meeting_post_like;

import petPeople.pet.domain.meeting.entity.MeetingPostLike;

import java.util.List;
import java.util.Optional;

public interface MeetingPostLikeCustomRepository {
    Optional<MeetingPostLike> findByMemberIdAndMeetingPostId(Long memberId, Long meetingPostId);
    void deleteByMemberIdAndMeetingPostId(Long memberId, Long meetingPostId);
    long countByMeetingPostsId(Long meetingPostId);
    List<MeetingPostLike> findByMeetingPostIds(List<Long> meetingPostIds);
    Long deleteByMeetingPostId(Long meetingPostId);
}
