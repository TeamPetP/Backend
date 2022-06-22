package petPeople.pet.domain.meeting.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import petPeople.pet.domain.meeting.entity.MeetingCommentLike;
import petPeople.pet.domain.meeting.entity.QMeetingCommentLike;

import javax.persistence.EntityManager;
import java.util.List;

import static petPeople.pet.domain.meeting.entity.QMeetingCommentLike.*;

@RequiredArgsConstructor
public class MeetingCommentLikeCustomRepositoryImpl implements MeetingCommentLikeCustomRepository{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public List<MeetingCommentLike> findByMeetingPostId(Long meetingPostId) {
        return queryFactory
                .selectFrom(meetingCommentLike)
                .where(meetingCommentLike.meetingComment.meetingPost.id.eq(meetingPostId))
                .fetch();
    }
}
