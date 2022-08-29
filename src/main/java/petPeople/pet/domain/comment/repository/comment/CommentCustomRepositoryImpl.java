package petPeople.pet.domain.comment.repository.comment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import petPeople.pet.domain.comment.entity.Comment;

import javax.persistence.EntityManager;
import java.util.List;

import static petPeople.pet.domain.comment.entity.QComment.*;
import static petPeople.pet.domain.member.entity.QMember.*;
import static petPeople.pet.domain.post.entity.QPost.post;

@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public List<Comment> findAllByIdWithFetchJoinMemberPaging(Long postId) {
        return queryFactory
                .selectFrom(comment)
                .join(comment.post, post).fetchJoin()
                .join(comment.member, member).fetchJoin()
                .where(comment.post.id.eq(postId), comment.parent.isNull())
                .orderBy(comment.createdDate.asc())
                .fetch();
    }

    @Override
    public List<Comment> findByPostIds(List<Long> ids) {
        return queryFactory
                .selectFrom(comment)
                .where(comment.post.id.in(ids))
                .fetch();
    }

    @Override
    public List<Comment> findByPostId(Long postId) {
        return queryFactory
                .selectFrom(comment)
                .where(comment.post.id.eq(postId))
                .fetch();
    }

    @Override
    public Long countByPostId(Long postId) {
        return queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.post.id.eq(postId))
                .fetchOne();
    }

    @Override
    public void deleteCommentByPostId(Long postId) {
        queryFactory
                .delete(comment)
                .where(comment.post.id.eq(postId))
                .execute();

        em.flush();
        em.clear();
    }

    @Override
    public void deleteByCommentIds(List<Long> commentIds) {
        queryFactory
                .delete(comment)
                .where(comment.id.in(commentIds))
                .execute();

        em.flush();
        em.clear();

    }
}
