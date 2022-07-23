package petPeople.pet.domain.meeting.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMeetingComment is a Querydsl query type for MeetingComment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMeetingComment extends EntityPathBase<MeetingComment> {

    private static final long serialVersionUID = -1631408415L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMeetingComment meetingComment = new QMeetingComment("meetingComment");

    public final petPeople.pet.domain.base.QBaseTimeEntity _super = new petPeople.pet.domain.base.QBaseTimeEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final ListPath<MeetingComment, QMeetingComment> meetingCommentChild = this.<MeetingComment, QMeetingComment>createList("meetingCommentChild", MeetingComment.class, QMeetingComment.class, PathInits.DIRECT2);

    public final QMeetingComment meetingCommentParent;

    public final QMeetingPost meetingPost;

    public final petPeople.pet.domain.member.entity.QMember member;

    public QMeetingComment(String variable) {
        this(MeetingComment.class, forVariable(variable), INITS);
    }

    public QMeetingComment(Path<? extends MeetingComment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMeetingComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMeetingComment(PathMetadata metadata, PathInits inits) {
        this(MeetingComment.class, metadata, inits);
    }

    public QMeetingComment(Class<? extends MeetingComment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.meetingCommentParent = inits.isInitialized("meetingCommentParent") ? new QMeetingComment(forProperty("meetingCommentParent"), inits.get("meetingCommentParent")) : null;
        this.meetingPost = inits.isInitialized("meetingPost") ? new QMeetingPost(forProperty("meetingPost"), inits.get("meetingPost")) : null;
        this.member = inits.isInitialized("member") ? new petPeople.pet.domain.member.entity.QMember(forProperty("member")) : null;
    }

}

