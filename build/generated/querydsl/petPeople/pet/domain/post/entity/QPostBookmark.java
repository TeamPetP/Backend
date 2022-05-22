package petPeople.pet.domain.post.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostBookmark is a Querydsl query type for PostBookmark
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostBookmark extends EntityPathBase<PostBookmark> {

    private static final long serialVersionUID = 658085806L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostBookmark postBookmark = new QPostBookmark("postBookmark");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final petPeople.pet.domain.member.entity.QMember member;

    public final QPost post;

    public QPostBookmark(String variable) {
        this(PostBookmark.class, forVariable(variable), INITS);
    }

    public QPostBookmark(Path<? extends PostBookmark> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostBookmark(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostBookmark(PathMetadata metadata, PathInits inits) {
        this(PostBookmark.class, metadata, inits);
    }

    public QPostBookmark(Class<? extends PostBookmark> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new petPeople.pet.domain.member.entity.QMember(forProperty("member")) : null;
        this.post = inits.isInitialized("post") ? new QPost(forProperty("post"), inits.get("post")) : null;
    }

}

