package petPeople.pet.domain.meeting.repository.custom;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import petPeople.pet.domain.meeting.entity.Meeting;

public interface MeetingCustomRepository {
    Slice<Meeting> findAllSlicingWithFetchJoinMember(Pageable pageable);
    Slice<Meeting> findAllSlicingByMemberId(Pageable pageable, Long memberId);
}
