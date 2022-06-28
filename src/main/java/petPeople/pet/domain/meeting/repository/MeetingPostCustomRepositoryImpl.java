package petPeople.pet.domain.meeting.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import petPeople.pet.domain.meeting.entity.MeetingPost;
import petPeople.pet.domain.meeting.repository.MeetingPostCustomRepository;

import java.util.List;

import static petPeople.pet.domain.meeting.entity.QMeetingPost.*;

@RequiredArgsConstructor
public class MeetingPostCustomRepositoryImpl implements MeetingPostCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<MeetingPost> findAllSliceByMeetingId(Long meetingId, Pageable pageable) {
        List<MeetingPost> content = queryFactory
                .select(meetingPost)
                .from(meetingPost)
                .where(meetingPost.meeting.id.eq(meetingId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(meetingPost.createdDate.desc())
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }
}
