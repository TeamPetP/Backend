package petPeople.pet.domain.meeting.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMeetingCommentLike is a Querydsl query type for MeetingCommentLike
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMeetingCommentLike extends EntityPathBase<MeetingCommentLike> {

    private static final long serialVersionUID = 239237656L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMeetingCommentLike meetingCommentLike = new QMeetingCommentLike("meetingCommentLike");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMeetingComment meetingComment;

    public final petPeople.pet.domain.member.entity.QMember member;

    public QMeetingCommentLike(String variable) {
        this(MeetingCommentLike.class, forVariable(variable), INITS);
    }

    public QMeetingCommentLike(Path<? extends MeetingCommentLike> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMeetingCommentLike(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMeetingCommentLike(PathMetadata metadata, PathInits inits) {
        this(MeetingCommentLike.class, metadata, inits);
    }

    public QMeetingCommentLike(Class<? extends MeetingCommentLike> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.meetingComment = inits.isInitialized("meetingComment") ? new QMeetingComment(forProperty("meetingComment"), inits.get("meetingComment")) : null;
        this.member = inits.isInitialized("member") ? new petPeople.pet.domain.member.entity.QMember(forProperty("member")) : null;
    }

}

