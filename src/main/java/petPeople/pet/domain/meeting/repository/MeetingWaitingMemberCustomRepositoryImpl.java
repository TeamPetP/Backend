package petPeople.pet.domain.meeting.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import petPeople.pet.domain.meeting.entity.JoinRequestStatus;
import petPeople.pet.domain.meeting.entity.MeetingWaitingMember;
import petPeople.pet.domain.meeting.entity.QMeetingWaitingMember;
import petPeople.pet.domain.meeting.repository.MeetingWaitingMemberCustomRepository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static petPeople.pet.domain.meeting.entity.QMeeting.*;
import static petPeople.pet.domain.meeting.entity.QMeetingWaitingMember.*;
import static petPeople.pet.domain.meeting.entity.QMeetingWaitingMember.meetingWaitingMember;
import static petPeople.pet.domain.member.entity.QMember.*;

@AllArgsConstructor
public class MeetingWaitingMemberCustomRepositoryImpl implements MeetingWaitingMemberCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;
    @Override
    public List<MeetingWaitingMember> findAllByMeetingIdFetchJoinMember(Long meetingId) {
        return queryFactory
                .selectFrom(meetingWaitingMember)
                .join(meetingWaitingMember.member, member).fetchJoin()
                .where(meetingWaitingMember.meeting.id.eq(meetingId), meetingWaitingMember.joinRequestStatus.eq(JoinRequestStatus.WAITING))
                .fetch();
    }

    @Override
    public Optional<MeetingWaitingMember> findAllByMeetingIdAndMemberId(Long meetingId, Long memberId) {
        MeetingWaitingMember content = queryFactory
                .selectFrom(meetingWaitingMember)
                .where(meetingWaitingMember.meeting.id.eq(meetingId),
                        meetingWaitingMember.member.id.eq(memberId),
                        meetingWaitingMember.joinRequestStatus.eq(JoinRequestStatus.WAITING))
                .fetchOne();
        return Optional.ofNullable(content);
    }

    @Override
    public Optional<MeetingWaitingMember> findByMeetingIdAndMemberIdFetchJoinMember(Long meetingId, Long memberId) {
        MeetingWaitingMember result = queryFactory
                .selectFrom(meetingWaitingMember)
                .join(meetingWaitingMember.member, member).fetchJoin()
                .where(meetingWaitingMember.meeting.id.eq(meetingId), meetingWaitingMember.member.id.eq(memberId), meetingWaitingMember.joinRequestStatus.eq(JoinRequestStatus.WAITING))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public Slice<MeetingWaitingMember> findAllByMemberIdFetchJoinMemberAndMeeting(Pageable pageable, Long memberId) {

        List<MeetingWaitingMember> content = queryFactory
                .selectFrom(meetingWaitingMember)
                .join(meetingWaitingMember.member, member).fetchJoin()
                .join(meetingWaitingMember.meeting, meeting).fetchJoin()
                .where(meetingWaitingMember.member.id.eq(memberId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(meetingWaitingMember.createdDate.desc())
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public void deleteByMeetingIdAndMemberId(Long meetingId, Long memberId) {
        queryFactory
                .delete(meetingWaitingMember)
                .where(meetingWaitingMember.meeting.id.eq(meetingId), meetingWaitingMember.member.id.eq(memberId))
                .execute();
        em.flush();
        em.clear();
    }

    @Override
    public Optional<MeetingWaitingMember> findByMemberIdAndMeetingId(Long memberId, Long meetingId) {

        MeetingWaitingMember content = queryFactory
                .select(meetingWaitingMember)
                .from(meetingWaitingMember)
                .where(meetingWaitingMember.member.id.eq(memberId), meetingWaitingMember.meeting.id.eq(meetingId))
                .fetchOne();

        return Optional.ofNullable(content);
    }
}
