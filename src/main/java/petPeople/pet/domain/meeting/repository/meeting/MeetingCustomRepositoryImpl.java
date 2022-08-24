package petPeople.pet.domain.meeting.repository.meeting;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.util.StringUtils;
import petPeople.pet.controller.post.model.MeetingParameter;
import petPeople.pet.domain.meeting.entity.Meeting;

import java.util.List;

import static petPeople.pet.domain.meeting.entity.QMeeting.*;
import static petPeople.pet.domain.meeting.entity.QMeetingMember.*;
import static petPeople.pet.domain.member.entity.QMember.*;

@RequiredArgsConstructor
public class MeetingCustomRepositoryImpl implements MeetingCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Meeting> findAllSlicingWithFetchJoinMember(Pageable pageable, MeetingParameter meetingParameter) {
        List<Meeting> content = queryFactory
                .select(meeting)
                .from(meeting)
                .where(isDoNameContain(meetingParameter.getDosi()), isOpened(meetingParameter.getIsOpened()),
                        contentAndTitleEq(meetingParameter.getContent()), meetingHostIsEq(meetingParameter.getMeetingHost()))
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

    private BooleanExpression meetingHostIsEq(String meetingHost) {
        return StringUtils.hasText(meetingHost) ? meeting.member.nickname.contains(meetingHost) : null;
    }

    private BooleanExpression contentAndTitleEq(String content) {
        return StringUtils.hasText(content) ? meeting.content.contains(content).or(meeting.title.eq(content)) : null;
    }

    private BooleanExpression isOpened(String isOpened) {
        if (StringUtils.hasText(isOpened)) {
            if (isOpened.equals("true"))
                return meeting.isOpened.eq(true);
            else
                return meeting.isOpened.eq(false);
        } else {
            return null;
        }
    }

    private BooleanExpression isDoNameContain(String dosi) {
        return StringUtils.hasText(dosi) ? meeting.doName.contains(dosi) : null;
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

    @Override
    public Long countByMemberId(Long memberId) {
        return queryFactory
                .select(meeting.count())
                .from(meetingMember)
                .where(meetingMember.member.id.eq(memberId))
                .fetchOne();
    }
}
