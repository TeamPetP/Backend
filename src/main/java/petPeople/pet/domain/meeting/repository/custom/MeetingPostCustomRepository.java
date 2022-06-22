package petPeople.pet.domain.meeting.repository.custom;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import petPeople.pet.domain.meeting.entity.MeetingPost;

public interface MeetingPostCustomRepository {

    Slice<MeetingPost> findAllSliceByMeetingId(Long meetingId, Pageable pageable);

}
