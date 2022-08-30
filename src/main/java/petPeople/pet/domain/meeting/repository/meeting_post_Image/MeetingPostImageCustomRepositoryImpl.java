package petPeople.pet.domain.meeting.repository.meeting_post_Image;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import petPeople.pet.domain.meeting.entity.MeetingPostImage;

import javax.persistence.EntityManager;
import java.util.List;

import static petPeople.pet.domain.meeting.entity.QMeetingPostImage.*;

@RequiredArgsConstructor
public class MeetingPostImageCustomRepositoryImpl implements MeetingPostImageCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public List<MeetingPostImage> findAllMeetingPostImageByMeetingPostId(Long meetingPostId) {
        List<MeetingPostImage> fetch = queryFactory
                .selectFrom(meetingPostImage)
                .where(meetingPostImage.meetingPost.id.eq(meetingPostId))
                .fetch();

        return fetch;
    }

    @Override
    public List<MeetingPostImage> findAllByMeetingPostIds(List<Long> meetingPostIds) {
        return queryFactory
                .selectFrom(meetingPostImage)
                .where(meetingPostImage.meetingPost.id.in(meetingPostIds))
                .fetch();
    }

    @Override
    public Long deleteByMeetingPostId(Long meetingPostId) {
        long count = queryFactory
                .delete(meetingPostImage)
                .where(meetingPostImage.meetingPost.id.eq(meetingPostId))
                .execute();

        em.flush();
        em.clear();

        return count;
    }

    @Override
    public List<MeetingPostImage> findAllByMeetingId(Long meetingId) {
        return queryFactory
                .selectFrom(meetingPostImage)
                .where(meetingPostImage.meetingPost.meeting.id.eq(meetingId))
                .fetch();
    }
}
