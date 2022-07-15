package petPeople.pet.domain.meeting.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMeetingImageFile is a Querydsl query type for MeetingImageFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMeetingImageFile extends EntityPathBase<MeetingImageFile> {

    private static final long serialVersionUID = 496014713L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMeetingImageFile meetingImageFile = new QMeetingImageFile("meetingImageFile");

    public final petPeople.pet.domain.base.QBaseTimeEntity _super = new petPeople.pet.domain.base.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imgFileUrl = createString("imgFileUrl");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final QMeeting meeting;

    public QMeetingImageFile(String variable) {
        this(MeetingImageFile.class, forVariable(variable), INITS);
    }

    public QMeetingImageFile(Path<? extends MeetingImageFile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMeetingImageFile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMeetingImageFile(PathMetadata metadata, PathInits inits) {
        this(MeetingImageFile.class, metadata, inits);
    }

    public QMeetingImageFile(Class<? extends MeetingImageFile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.meeting = inits.isInitialized("meeting") ? new QMeeting(forProperty("meeting"), inits.get("meeting")) : null;
    }

}

