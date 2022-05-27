package petPeople.pet.domain.meeting.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMeeting is a Querydsl query type for Meeting
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMeeting extends EntityPathBase<Meeting> {

    private static final long serialVersionUID = -220370530L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMeeting meeting = new QMeeting("meeting");

    public final petPeople.pet.domain.base.QBaseTimeEntity _super = new petPeople.pet.domain.base.QBaseTimeEntity(this);

    public final EnumPath<Category> category = createEnum("category", Category.class);

    public final StringPath conditions = createString("conditions");

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath doName = createString("doName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isOpened = createBoolean("isOpened");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath location = createString("location");

    public final NumberPath<Integer> maxPeople = createNumber("maxPeople", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> meetingDate = createDateTime("meetingDate", java.time.LocalDateTime.class);

    public final EnumPath<MeetingType> meetingType = createEnum("meetingType", MeetingType.class);

    public final petPeople.pet.domain.member.entity.QMember member;

    public final StringPath period = createString("period");

    public final EnumPath<Sex> sex = createEnum("sex", Sex.class);

    public final StringPath sigungu = createString("sigungu");

    public final StringPath title = createString("title");

    public QMeeting(String variable) {
        this(Meeting.class, forVariable(variable), INITS);
    }

    public QMeeting(Path<? extends Meeting> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMeeting(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMeeting(PathMetadata metadata, PathInits inits) {
        this(Meeting.class, metadata, inits);
    }

    public QMeeting(Class<? extends Meeting> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new petPeople.pet.domain.member.entity.QMember(forProperty("member")) : null;
    }

}

