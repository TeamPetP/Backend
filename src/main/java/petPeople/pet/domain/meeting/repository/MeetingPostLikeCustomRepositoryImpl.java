package petPeople.pet.domain.meeting.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import petPeople.pet.domain.meeting.entity.MeetingPostLike;
import petPeople.pet.domain.meeting.entity.QMeetingPostLike;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static petPeople.pet.domain.meeting.entity.QMeetingPostLike.*;

@RequiredArgsConstructor
public class MeetingPostLikeCustomRepositoryImpl implements MeetingPostLikeCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public Optional<MeetingPostLike> findByMemberIdAndMeetingPostId(Long memberId, Long meetingPostId) {

        MeetingPostLike content = queryFactory
                .selectFrom(meetingPostLike)
                .where(meetingPostLike.member.id.eq(memberId), meetingPostLike.meetingPost.id.eq(meetingPostId))
                .fetchOne();

        return Optional.ofNullable(content);
    }

    @Override
    public void deleteByMemberIdAndMeetingPostId(Long memberId, Long meetingPostId) {
        queryFactory
                .delete(meetingPostLike)
                .where(meetingPostLike.member.id.eq(memberId), meetingPostLike.meetingPost.id.eq(meetingPostId))
                .execute();
    }

    @Override
    public long countByMeetingPostsId(Long meetingPostId) {
        return queryFactory
                .select(meetingPostLike.count())
                .from(meetingPostLike)
                .where(meetingPostLike.meetingPost.id.eq(meetingPostId))
                .fetchOne();
    }

    @Override
    public List<MeetingPostLike> findByMeetingPostIds(List<Long> meetingPostIds) {
        return queryFactory
                .selectFrom(meetingPostLike)
                .where(meetingPostLike.meetingPost.id.in(meetingPostIds))
                .fetch();
    }

    @Override
    public void deleteByMeetingPostId(Long meetingPostId) {
        queryFactory
                .delete(meetingPostLike)
                .where(meetingPostLike.meetingPost.id.eq(meetingPostId))
                .execute();

        em.flush();
        em.clear();
    }
}
