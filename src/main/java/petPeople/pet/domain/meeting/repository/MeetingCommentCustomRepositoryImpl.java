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

import static petPeople.pet.domain.meeting.entity.QMeetingComment.*;

@RequiredArgsConstructor
public class MeetingCommentCustomRepositoryImpl implements MeetingCommentCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public Slice<MeetingComment> findByMeetingPostId(Pageable pageable, Long meetingPostId) {
        List<MeetingComment> content = queryFactory
                .selectFrom(meetingComment)
                .where(meetingComment.meetingPost.id.eq(meetingPostId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(meetingComment.createdDate.desc())
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }
}
