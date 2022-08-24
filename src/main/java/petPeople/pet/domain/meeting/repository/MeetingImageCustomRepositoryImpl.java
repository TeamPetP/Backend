package petPeople.pet.domain.meeting.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import petPeople.pet.domain.meeting.entity.MeetingImage;
import petPeople.pet.domain.meeting.repository.MeetingImageCustomRepository;

import javax.persistence.EntityManager;
import java.util.List;

import static petPeople.pet.domain.meeting.entity.QMeetingImage.*;

@RequiredArgsConstructor
public class MeetingImageCustomRepositoryImpl implements MeetingImageCustomRepository {

    private final EntityManager em;
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
    public Long deleteByMeetingId(Long meetingId) {
        long count = queryFactory
                .delete(meetingImage)
                .where(meetingImage.meeting.id.eq(meetingId))
                .execute();

        em.flush();
        em.clear();

        return count;
    }
}
