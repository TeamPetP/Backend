package petPeople.pet.domain.meeting.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import petPeople.pet.domain.meeting.entity.MeetingMember;
import petPeople.pet.domain.meeting.repository.MeetingMemberCustomRepository;

import javax.persistence.EntityManager;
import java.util.List;

import static petPeople.pet.domain.meeting.entity.QMeetingMember.*;

@RequiredArgsConstructor
public class MeetingMemberCustomRepositoryImpl implements MeetingMemberCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public List<MeetingMember> findByMeetingIds(List<Long> meetingIds) {
        return queryFactory
                .selectFrom(meetingMember)
                .where(meetingMember.meeting.id.in(meetingIds))
                .fetch();
    }

    @Override
    public List<MeetingMember> findByMeetingId(Long meetingId) {
        return queryFactory
                .selectFrom(meetingMember)
                .where(meetingMember.meeting.id.eq(meetingId))
                .fetch();
    }

    @Override
    public Long countByMeetingId(Long meetingId) {
        return queryFactory
                .select(meetingMember.count())
                .from(meetingMember)
                .where(meetingMember.meeting.id.eq(meetingId))
                .fetchOne();
    }

    @Override
    public Long countByMemberId(Long memberId) {
        return queryFactory
                .select(meetingMember.count())
                .from(meetingMember)
                .where(meetingMember.member.id.eq(memberId))
                .fetchOne();
    }

    @Override
    public Long deleteByMeetingIdAndMemberId(Long meetingId, Long memberId) {
        long count = queryFactory
                .delete(meetingMember)
                .where(meetingMember.meeting.id.eq(meetingId), meetingMember.member.id.eq(memberId))
                .execute();

        em.flush();
        em.clear();

        return count;
    }
}
