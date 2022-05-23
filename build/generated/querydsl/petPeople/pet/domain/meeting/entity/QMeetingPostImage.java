package petPeople.pet.domain.meeting.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMeetingPostImage is a Querydsl query type for MeetingPostImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMeetingPostImage extends EntityPathBase<MeetingPostImage> {

    private static final long serialVersionUID = -937939811L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMeetingPostImage meetingPostImage = new QMeetingPostImage("meetingPostImage");

    public final petPeople.pet.domain.base.QBaseTimeEntity _super = new petPeople.pet.domain.base.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imgUrl = createString("imgUrl");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final QMeetingPost meetingPost;

    public final petPeople.pet.domain.member.entity.QMember member;

    public QMeetingPostImage(String variable) {
        this(MeetingPostImage.class, forVariable(variable), INITS);
    }

    public QMeetingPostImage(Path<? extends MeetingPostImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMeetingPostImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMeetingPostImage(PathMetadata metadata, PathInits inits) {
        this(MeetingPostImage.class, metadata, inits);
    }

    public QMeetingPostImage(Class<? extends MeetingPostImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.meetingPost = inits.isInitialized("meetingPost") ? new QMeetingPost(forProperty("meetingPost"), inits.get("meetingPost")) : null;
        this.member = inits.isInitialized("member") ? new petPeople.pet.domain.member.entity.QMember(forProperty("member")) : null;
    }

}

