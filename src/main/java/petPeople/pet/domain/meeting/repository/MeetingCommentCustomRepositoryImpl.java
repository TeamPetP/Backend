package petPeople.pet.domain.meeting.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import petPeople.pet.domain.meeting.entity.MeetingComment;
import petPeople.pet.domain.meeting.repository.MeetingCommentCustomRepository;

import javax.persistence.EntityManager;

import java.util.List;

import static petPeople.pet.domain.comment.entity.QComment.comment;
import static petPeople.pet.domain.meeting.entity.QMeetingComment.*;

@RequiredArgsConstructor
public class MeetingCommentCustomRepositoryImpl implements MeetingCommentCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public List<MeetingComment> findByMeetingPostId(Long meetingPostId) {
        return queryFactory
                .selectFrom(meetingComment)
                .where(meetingComment.meetingPost.id.eq(meetingPostId), meetingComment.meetingCommentParent.isNull())
                .orderBy(meetingComment.createdDate.desc())
                .fetch();
    }

    @Override
    public Long countByMeetingPostId(Long meetingPostId) {
        return queryFactory
                .select(meetingComment.count())
                .from(meetingComment)
                .where(meetingComment.meetingPost.id.eq(meetingPostId))
                .fetchOne();
    }

    @Override
    public void deleteMeetingCommentByMeetingPostId(Long meetingPostId) {
        queryFactory
                .delete(meetingComment)
                .where(meetingComment.meetingPost.id.eq(meetingPostId))
                .execute();
        em.clear();
        em.flush();
    }


    @Override
    public void deleteMeetingCommentByIds(List<Long> meetingPostIds) {
        queryFactory
                .delete(meetingComment)
                .where(meetingComment.id.in(meetingPostIds))
                .execute();
        em.clear();
        em.flush();
    }

    @Override
    public void deleteMeetingCommentById(Long meetingCommentId) {
        queryFactory
                .delete(meetingComment)
                .where(meetingComment.id.eq(meetingCommentId))
                .execute();
        em.clear();
        em.flush();
    }
}
