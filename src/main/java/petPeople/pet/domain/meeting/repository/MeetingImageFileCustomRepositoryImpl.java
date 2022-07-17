package petPeople.pet.domain.meeting.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import petPeople.pet.domain.meeting.entity.MeetingImageFile;
import petPeople.pet.domain.meeting.entity.QMeetingImageFile;

import java.util.List;

import static petPeople.pet.domain.meeting.entity.QMeetingImage.meetingImage;
import static petPeople.pet.domain.meeting.entity.QMeetingImageFile.meetingImageFile;

@RequiredArgsConstructor
public class MeetingImageFileCustomRepositoryImpl implements MeetingImageFileCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MeetingImageFile> findByMeetingIds(List<Long> meetingIds) {
        return queryFactory
                .selectFrom(meetingImageFile)
                .where(meetingImageFile.meeting.id.in(meetingIds))
                .fetch();
    }

    @Override
    public List<MeetingImageFile> findByMeetingId(Long meetingId) {
        return queryFactory
                .selectFrom(meetingImageFile)
                .where(meetingImageFile.meeting.id.eq(meetingId))
                .fetch();
    }

    @Override
    public void deleteByMeetingId(Long meetingId) {
        queryFactory
                .delete(meetingImageFile)
                .where(meetingImageFile.meeting.id.eq(meetingId))
                .execute();
    }
}
