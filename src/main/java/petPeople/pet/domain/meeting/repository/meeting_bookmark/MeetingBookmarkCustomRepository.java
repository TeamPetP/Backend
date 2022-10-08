package petPeople.pet.domain.meeting.repository.meeting_bookmark;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import petPeople.pet.domain.meeting.entity.MeetingBookmark;

import java.util.Optional;

public interface MeetingBookmarkCustomRepository {

    Optional<MeetingBookmark> findByMemberIdAndMeetingId(Long memberId, Long meetingId);
    Long deleteByMemberIdAndMeetingId(Long memberId, Long meetingId);
    Slice<MeetingBookmark> findByMemberIdWithFetchJoinMeetingSlicing(Long memberId, Pageable pageable);
    Long countByMemberId(Long id);
}