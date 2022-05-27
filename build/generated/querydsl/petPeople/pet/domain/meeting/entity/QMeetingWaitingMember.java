package petPeople.pet.domain.meeting.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMeetingWaitingMember is a Querydsl query type for MeetingWaitingMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMeetingWaitingMember extends EntityPathBase<MeetingWaitingMember> {

    private static final long serialVersionUID = -1151388087L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMeetingWaitingMember meetingWaitingMember = new QMeetingWaitingMember("meetingWaitingMember");

    public final petPeople.pet.domain.base.QBaseTimeEntity _super = new petPeople.pet.domain.base.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<JoinRequestStatus> joinRequestStatus = createEnum("joinRequestStatus", JoinRequestStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final QMeeting meeting;

    public final petPeople.pet.domain.member.entity.QMember member;

    public QMeetingWaitingMember(String variable) {
        this(MeetingWaitingMember.class, forVariable(variable), INITS);
    }

    public QMeetingWaitingMember(Path<? extends MeetingWaitingMember> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMeetingWaitingMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMeetingWaitingMember(PathMetadata metadata, PathInits inits) {
        this(MeetingWaitingMember.class, metadata, inits);
    }

    public QMeetingWaitingMember(Class<? extends MeetingWaitingMember> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.meeting = inits.isInitialized("meeting") ? new QMeeting(forProperty("meeting"), inits.get("meeting")) : null;
        this.member = inits.isInitialized("member") ? new petPeople.pet.domain.member.entity.QMember(forProperty("member")) : null;
    }

}

