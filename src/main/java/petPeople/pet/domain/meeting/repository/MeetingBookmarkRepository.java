package petPeople.pet.domain.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.comment.entity.CommentLike;
import petPeople.pet.domain.comment.repository.CommentLikeCustomRepository;
import petPeople.pet.domain.meeting.entity.MeetingBookmark;

public interface MeetingBookmarkRepository extends JpaRepository<MeetingBookmark, Long>, MeetingBookmarkCustomRepository {

}
