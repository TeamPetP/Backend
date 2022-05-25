package petPeople.pet.domain.meeting.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import petPeople.pet.domain.meeting.entity.JoinRequestStatus;
import petPeople.pet.domain.meeting.entity.MeetingWaitingMember;
import petPeople.pet.domain.meeting.entity.QMeetingMember;
import petPeople.pet.domain.meeting.entity.QMeetingWaitingMember;
import petPeople.pet.domain.member.entity.QMember;

import java.util.List;
import java.util.Optional;

import static petPeople.pet.domain.meeting.entity.QMeetingWaitingMember.*;
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
        MeetingWaitingMember meetingWaitingMember = queryFactory
                .selectFrom(QMeetingWaitingMember.meetingWaitingMember)
                .join(QMeetingWaitingMember.meetingWaitingMember.member, member).fetchJoin()
                .where(QMeetingWaitingMember.meetingWaitingMember.meeting.id.eq(meetingId), QMeetingWaitingMember.meetingWaitingMember.member.id.eq(memberId))
                .fetchOne();
        return Optional.ofNullable(meetingWaitingMember);
    }
}
