package petPeople.pet.domain.meeting.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMeetingBookmark is a Querydsl query type for MeetingBookmark
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMeetingBookmark extends EntityPathBase<MeetingBookmark> {

    private static final long serialVersionUID = -721226508L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMeetingBookmark meetingBookmark = new QMeetingBookmark("meetingBookmark");

    public final petPeople.pet.domain.base.QBaseTimeEntity _super = new petPeople.pet.domain.base.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final QMeeting meeting;

    public final petPeople.pet.domain.member.entity.QMember member;

    public QMeetingBookmark(String variable) {
        this(MeetingBookmark.class, forVariable(variable), INITS);
    }

    public QMeetingBookmark(Path<? extends MeetingBookmark> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMeetingBookmark(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMeetingBookmark(PathMetadata metadata, PathInits inits) {
        this(MeetingBookmark.class, metadata, inits);
    }

    public QMeetingBookmark(Class<? extends MeetingBookmark> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.meeting = inits.isInitialized("meeting") ? new QMeeting(forProperty("meeting"), inits.get("meeting")) : null;
        this.member = inits.isInitialized("member") ? new petPeople.pet.domain.member.entity.QMember(forProperty("member")) : null;
    }

}

