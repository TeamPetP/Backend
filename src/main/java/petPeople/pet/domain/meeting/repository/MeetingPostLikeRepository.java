package petPeople.pet.domain.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.meeting.entity.MeetingPostImage;
import petPeople.pet.domain.meeting.entity.MeetingPostLike;

public interface MeetingPostLikeRepository extends JpaRepository<MeetingPostLike, Long>, MeetingPostLikeCustomRepository {
}
