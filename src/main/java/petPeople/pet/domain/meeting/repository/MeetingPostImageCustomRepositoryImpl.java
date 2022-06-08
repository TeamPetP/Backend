package petPeople.pet.domain.meeting.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import petPeople.pet.domain.meeting.entity.MeetingPost;
import petPeople.pet.domain.meeting.entity.MeetingPostImage;

import java.util.List;

import static petPeople.pet.domain.meeting.entity.QMeetingPostImage.*;

@RequiredArgsConstructor
public class MeetingPostImageCustomRepositoryImpl implements MeetingPostImageCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MeetingPostImage> findAllMeetingPostImageByMeetingPostId(Long meetingPostId) {
        return queryFactory
                .selectFrom(meetingPostImage)
                .where(meetingPostImage.meetingPost.id.eq(meetingPostId))
                .fetch();
    }

    @Override
    public List<MeetingPostImage> findAllByMeetingPostIds(List<Long> meetingPostIds) {
        return queryFactory
                .selectFrom(meetingPostImage)
                .where(meetingPostImage.meetingPost.id.in(meetingPostIds))
                .fetch();
    }
}