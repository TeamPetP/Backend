package petPeople.pet.domain.meeting.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import petPeople.pet.domain.meeting.entity.MeetingImage;
import petPeople.pet.domain.meeting.repository.custom.MeetingImageCustomRepository;

import java.util.List;

import static petPeople.pet.domain.meeting.entity.QMeetingImage.*;

@RequiredArgsConstructor
public class MeetingImageCustomRepositoryImpl implements MeetingImageCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MeetingImage> findByMeetingIds(List<Long> meetingIds) {
        return queryFactory
                .selectFrom(meetingImage)
                .where(meetingImage.meeting.id.in(meetingIds))
                .fetch();
    }

    @Override
    public List<MeetingImage> findByMeetingId(Long meetingId) {
        return queryFactory
                .selectFrom(meetingImage)
                .where(meetingImage.meeting.id.eq(meetingId))
                .fetch();
    }

    @Override
    public void deleteByMeetingId(Long meetingId) {
        queryFactory
                .delete(meetingImage)
                .where(meetingImage.meeting.id.eq(meetingId))
                .execute();
    }
}
