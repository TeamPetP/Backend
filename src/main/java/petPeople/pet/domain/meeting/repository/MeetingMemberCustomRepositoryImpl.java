package petPeople.pet.domain.meeting.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import petPeople.pet.domain.meeting.entity.MeetingMember;
import petPeople.pet.domain.meeting.entity.QMeetingMember;

import java.util.List;

import static petPeople.pet.domain.meeting.entity.QMeetingMember.*;

@RequiredArgsConstructor
public class MeetingMemberCustomRepositoryImpl implements MeetingMemberCustomRepository {

    private final JPAQueryFactory queryFactory;

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
}
