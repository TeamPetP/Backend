package petPeople.pet.domain.meeting.repository.meeting;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import petPeople.pet.controller.post.model.MeetingParameter;
import petPeople.pet.domain.meeting.entity.Meeting;
import petPeople.pet.domain.member.entity.Member;

public interface MeetingCustomRepository {
    Slice<Meeting> findAllSlicingWithFetchJoinMember(Pageable pageable, MeetingParameter meetingParameter);
    Slice<Meeting> findAllSlicingByMemberId(Pageable pageable, Long memberId);
    Long countByMemberId(Long memberId);
}
