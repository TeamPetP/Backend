package petPeople.pet.domain.meeting.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import petPeople.pet.domain.meeting.entity.JoinRequestStatus;
import petPeople.pet.domain.meeting.entity.MeetingWaitingMember;

import java.util.List;
import java.util.Optional;

import static petPeople.pet.domain.meeting.entity.QMeetingWaitingMember.meetingWaitingMember;
import static petPeople.pet.domain.member.entity.QMember.*;

@AllArgsConstructor
public class MeetingWaitingMemberCustomRepositoryImpl implements MeetingWaitingMemberCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MeetingWaitingMember> findAllByMeetingId(Long meetingId) {
        return queryFactory
                .selectFrom(meetingWaitingMember)
                .join(meetingWaitingMember.member, member).fetchJoin()
                .where(meetingWaitingMember.meeting.id.eq(meetingId), meetingWaitingMember.joinRequestStatus.eq(JoinRequestStatus.WAITING))
                .fetch();
    }

    @Override
    public Optional<MeetingWaitingMember> findByMeetingIdAndMemberId(Long meetingId, Long memberId) {
        MeetingWaitingMember result = queryFactory
                .selectFrom(meetingWaitingMember)
                .join(meetingWaitingMember.member, member).fetchJoin()
                .where(meetingWaitingMember.meeting.id.eq(meetingId), meetingWaitingMember.member.id.eq(memberId), meetingWaitingMember.joinRequestStatus.eq(JoinRequestStatus.WAITING))
                .fetchOne();
        return Optional.ofNullable(result);
    }
}
