package petPeople.pet.domain.meeting.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import petPeople.pet.domain.meeting.entity.Meeting;
import petPeople.pet.domain.meeting.repository.custom.MeetingCustomRepository;

import java.util.List;

import static petPeople.pet.domain.meeting.entity.QMeeting.*;
import static petPeople.pet.domain.meeting.entity.QMeetingMember.*;
import static petPeople.pet.domain.member.entity.QMember.*;

@RequiredArgsConstructor
public class MeetingCustomRepositoryImpl implements MeetingCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Meeting> findAllSlicingWithFetchJoinMember(Pageable pageable) {
        List<Meeting> content = queryFactory
                .select(meeting)
                .from(meeting)
                .join(meeting.member, member).fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(meeting.createdDate.desc())
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<Meeting> findAllSlicingByMemberId(Pageable pageable, Long memberId) {
        List<Meeting> content = queryFactory
                .select(meeting)
                .from(meetingMember)
                .join(meetingMember.meeting, meeting)
                .where(meetingMember.member.id.eq(memberId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(meeting.createdDate.desc())
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

}
