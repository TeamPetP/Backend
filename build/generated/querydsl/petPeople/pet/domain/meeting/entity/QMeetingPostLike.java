package petPeople.pet.domain.meeting.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMeetingPostLike is a Querydsl query type for MeetingPostLike
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMeetingPostLike extends EntityPathBase<MeetingPostLike> {

    private static final long serialVersionUID = -1969832939L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMeetingPostLike meetingPostLike = new QMeetingPostLike("meetingPostLike");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMeetingPost meetingPost;

    public final petPeople.pet.domain.member.entity.QMember member;

    public QMeetingPostLike(String variable) {
        this(MeetingPostLike.class, forVariable(variable), INITS);
    }

    public QMeetingPostLike(Path<? extends MeetingPostLike> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMeetingPostLike(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMeetingPostLike(PathMetadata metadata, PathInits inits) {
        this(MeetingPostLike.class, metadata, inits);
    }

    public QMeetingPostLike(Class<? extends MeetingPostLike> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.meetingPost = inits.isInitialized("meetingPost") ? new QMeetingPost(forProperty("meetingPost"), inits.get("meetingPost")) : null;
        this.member = inits.isInitialized("member") ? new petPeople.pet.domain.member.entity.QMember(forProperty("member")) : null;
    }

}

