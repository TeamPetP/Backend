package petPeople.pet.domain.meeting.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import petPeople.pet.domain.comment.entity.CommentLike;
import petPeople.pet.domain.meeting.entity.MeetingBookmark;
import petPeople.pet.domain.post.entity.PostBookmark;

import java.util.List;
import java.util.Optional;

public interface MeetingBookmarkCustomRepository {

    Optional<MeetingBookmark> findByMemberIdAndMeetingId(Long memberId, Long meetingId);
    void deleteByMemberIdAndMeetingId(Long memberId, Long meetingId);
    Slice<MeetingBookmark> findByMemberIdWithFetchJoinMeeting(Long memberId, Pageable pageable);
    Long countByMemberId(Long id);
}
