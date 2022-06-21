package petPeople.pet.domain.meeting.repository;

import petPeople.pet.domain.meeting.entity.MeetingPostLike;

import java.util.Optional;

public interface MeetingPostLikeCustomRepository {
    Optional<MeetingPostLike> findByMemberIdAndMeetingPostId(Long memberId, Long meetingPostId);
    void deleteByMemberIdAndMeetingPostId(Long memberId, Long meetingPostId);
    long countByMeetingPostsId(Long meetingPostId);
}
