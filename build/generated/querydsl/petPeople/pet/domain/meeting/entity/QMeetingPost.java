package petPeople.pet.domain.meeting.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMeetingPost is a Querydsl query type for MeetingPost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMeetingPost extends EntityPathBase<MeetingPost> {

    private static final long serialVersionUID = 215578462L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMeetingPost meetingPost = new QMeetingPost("meetingPost");

    public final petPeople.pet.domain.base.QBaseTimeEntity _super = new petPeople.pet.domain.base.QBaseTimeEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final QMeeting meeting;

    public final petPeople.pet.domain.member.entity.QMember member;

    public final StringPath title = createString("title");

    public QMeetingPost(String variable) {
        this(MeetingPost.class, forVariable(variable), INITS);
    }

    public QMeetingPost(Path<? extends MeetingPost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMeetingPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMeetingPost(PathMetadata metadata, PathInits inits) {
        this(MeetingPost.class, metadata, inits);
    }

    public QMeetingPost(Class<? extends MeetingPost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.meeting = inits.isInitialized("meeting") ? new QMeeting(forProperty("meeting"), inits.get("meeting")) : null;
        this.member = inits.isInitialized("member") ? new petPeople.pet.domain.member.entity.QMember(forProperty("member")) : null;
    }

}

