package petPeople.pet.domain.notification.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotification is a Querydsl query type for Notification
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotification extends EntityPathBase<Notification> {

    private static final long serialVersionUID = -1749544850L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotification notification = new QNotification("notification");

    public final petPeople.pet.domain.base.QBaseTimeEntity _super = new petPeople.pet.domain.base.QBaseTimeEntity(this);

    public final petPeople.pet.domain.comment.entity.QComment comment;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isChecked = createBoolean("isChecked");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final petPeople.pet.domain.meeting.entity.QMeeting meeting;

    public final petPeople.pet.domain.meeting.entity.QMeetingComment meetingComment;

    public final EnumPath<petPeople.pet.domain.meeting.entity.vo.JoinRequestStatus> meetingJoinRequestFlag = createEnum("meetingJoinRequestFlag", petPeople.pet.domain.meeting.entity.vo.JoinRequestStatus.class);

    public final petPeople.pet.domain.meeting.entity.QMeetingPost meetingPost;

    public final petPeople.pet.domain.meeting.entity.QMeetingPost meetingWritePost;

    public final petPeople.pet.domain.member.entity.QMember member;

    public final petPeople.pet.domain.member.entity.QMember ownerMember;

    public final petPeople.pet.domain.post.entity.QPost post;

    public final petPeople.pet.domain.comment.entity.QComment writeComment;

    public final petPeople.pet.domain.meeting.entity.QMeetingComment writeMeetingComment;

    public QNotification(String variable) {
        this(Notification.class, forVariable(variable), INITS);
    }

    public QNotification(Path<? extends Notification> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotification(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotification(PathMetadata metadata, PathInits inits) {
        this(Notification.class, metadata, inits);
    }

    public QNotification(Class<? extends Notification> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.comment = inits.isInitialized("comment") ? new petPeople.pet.domain.comment.entity.QComment(forProperty("comment"), inits.get("comment")) : null;
        this.meeting = inits.isInitialized("meeting") ? new petPeople.pet.domain.meeting.entity.QMeeting(forProperty("meeting"), inits.get("meeting")) : null;
        this.meetingComment = inits.isInitialized("meetingComment") ? new petPeople.pet.domain.meeting.entity.QMeetingComment(forProperty("meetingComment"), inits.get("meetingComment")) : null;
        this.meetingPost = inits.isInitialized("meetingPost") ? new petPeople.pet.domain.meeting.entity.QMeetingPost(forProperty("meetingPost"), inits.get("meetingPost")) : null;
        this.meetingWritePost = inits.isInitialized("meetingWritePost") ? new petPeople.pet.domain.meeting.entity.QMeetingPost(forProperty("meetingWritePost"), inits.get("meetingWritePost")) : null;
        this.member = inits.isInitialized("member") ? new petPeople.pet.domain.member.entity.QMember(forProperty("member")) : null;
        this.ownerMember = inits.isInitialized("ownerMember") ? new petPeople.pet.domain.member.entity.QMember(forProperty("ownerMember")) : null;
        this.post = inits.isInitialized("post") ? new petPeople.pet.domain.post.entity.QPost(forProperty("post"), inits.get("post")) : null;
        this.writeComment = inits.isInitialized("writeComment") ? new petPeople.pet.domain.comment.entity.QComment(forProperty("writeComment"), inits.get("writeComment")) : null;
        this.writeMeetingComment = inits.isInitialized("writeMeetingComment") ? new petPeople.pet.domain.meeting.entity.QMeetingComment(forProperty("writeMeetingComment"), inits.get("writeMeetingComment")) : null;
    }

}

