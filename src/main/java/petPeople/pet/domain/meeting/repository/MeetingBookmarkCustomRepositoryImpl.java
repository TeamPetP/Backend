package petPeople.pet.domain.meeting.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import petPeople.pet.domain.comment.entity.CommentLike;
import petPeople.pet.domain.comment.entity.QCommentLike;
import petPeople.pet.domain.comment.repository.CommentLikeCustomRepository;
import petPeople.pet.domain.meeting.entity.MeetingBookmark;
import petPeople.pet.domain.meeting.entity.QMeeting;
import petPeople.pet.domain.meeting.entity.QMeetingBookmark;
import petPeople.pet.domain.post.entity.PostBookmark;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static petPeople.pet.domain.comment.entity.QCommentLike.commentLike;
import static petPeople.pet.domain.meeting.entity.QMeeting.*;
import static petPeople.pet.domain.meeting.entity.QMeetingBookmark.*;
import static petPeople.pet.domain.post.entity.QPost.post;
import static petPeople.pet.domain.post.entity.QPostBookmark.postBookmark;

@RequiredArgsConstructor
public class MeetingBookmarkCustomRepositoryImpl implements MeetingBookmarkCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public Optional<MeetingBookmark> findByMemberIdAndMeetingId(Long memberId, Long meetingId) {
        MeetingBookmark meetingBookmark = queryFactory
                .selectFrom(QMeetingBookmark.meetingBookmark)
                .where(QMeetingBookmark.meetingBookmark.member.id.eq(memberId), QMeetingBookmark.meetingBookmark.meeting.id.eq(meetingId))
                .fetchOne();
        return Optional.ofNullable(meetingBookmark);
    }

    @Override
    public void deleteByMemberIdAndMeetingId(Long memberId, Long meetingId) {
        queryFactory
                .delete(meetingBookmark)
                .where(meetingBookmark.member.id.eq(memberId), meetingBookmark.meeting.id.eq(meetingId))
                .execute();

        em.flush();
        em.clear();
    }

    @Override
    public Slice<MeetingBookmark> findByMemberIdWithFetchJoinMeeting(Long memberId, Pageable pageable) {
        List<MeetingBookmark> content = queryFactory
                .select(meetingBookmark)
                .from(meetingBookmark)
                .join(meetingBookmark.meeting, meeting).fetchJoin()
                .where(meetingBookmark.member.id.eq(memberId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(meetingBookmark.createdDate.desc())
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
                .select(meetingBookmark.count())
                .from(meetingBookmark)
                .where(meetingBookmark.member.id.eq(memberId))
                .fetchOne();
    }
}
