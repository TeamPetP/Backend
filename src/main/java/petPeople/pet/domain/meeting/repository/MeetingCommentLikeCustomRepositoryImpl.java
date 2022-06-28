package petPeople.pet.domain.meeting.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import petPeople.pet.domain.meeting.entity.MeetingCommentLike;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static petPeople.pet.domain.meeting.entity.QMeetingCommentLike.*;

@RequiredArgsConstructor
public class MeetingCommentLikeCustomRepositoryImpl implements MeetingCommentLikeCustomRepository{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public List<MeetingCommentLike> findByMeetingCommentIds(List<Long> meetingCommentIds) {
        return queryFactory
                .selectFrom(meetingCommentLike)
                .where(meetingCommentLike.meetingComment.id.in(meetingCommentIds))
                .fetch();
    }

    @Override
    public Optional<MeetingCommentLike> findByMeetingPostIdAndMemberId(Long meetingCommentId, Long memberId) {
        MeetingCommentLike content = queryFactory
                .selectFrom(meetingCommentLike)
                .where(meetingCommentLike.meetingComment.id.eq(meetingCommentId), meetingCommentLike.member.id.eq(memberId))
                .fetchOne();

        return Optional.ofNullable(content);
    }

    @Override
    public void deleteByMeetingCommentIdAndMemberId(Long meetingCommentId, Long memberId) {
        queryFactory
                .delete(meetingCommentLike)
                .where(meetingCommentLike.meetingComment.id.eq(meetingCommentId), meetingCommentLike.member.id.eq(memberId))
                .execute();

        em.flush();
        em.clear();
    }

    @Override
    public Long countByMeetingCommentId(Long meetingCommentId) {
        return queryFactory
                .select(meetingCommentLike.count())
                .from(meetingCommentLike)
                .where(meetingCommentLike.meetingComment.id.eq(meetingCommentId))
                .fetchOne();
    }
}
