package petPeople.pet.domain.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import petPeople.pet.domain.comment.entity.CommentLike;
import petPeople.pet.domain.comment.entity.QCommentLike;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static petPeople.pet.domain.comment.entity.QCommentLike.*;
import static petPeople.pet.domain.comment.entity.QCommentLike.commentLike;

@RequiredArgsConstructor
public class CommentLikeCustomRepositoryImpl implements CommentLikeCustomRepository{

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<CommentLike> findCommentLikesByCommentIds(List<Long> ids) {

        return queryFactory
                .selectFrom(commentLike)
                .where(commentLike.comment.id.in(ids))
                .fetch();
    }

    @Override
    public Long countByCommentId(Long commentId) {
        return queryFactory
                .select(commentLike.count())
                .from(commentLike)
                .where(commentLike.comment.id.eq(commentId))
                .fetchOne();
    }

    @Override
    public void deleteByCommentId(Long commentId) {
        queryFactory
                .delete(commentLike)
                .where(commentLike.comment.id.eq(commentId))
                .execute();

        em.flush();
        em.clear();
    }

    @Override
    public Optional<CommentLike> findCommentLikeByCommentIdAndMemberId(Long memberId, Long commentId) {
        CommentLike commentLike = queryFactory
                .select(QCommentLike.commentLike)
                .from(QCommentLike.commentLike)
                .where(QCommentLike.commentLike.comment.id.eq(commentId), QCommentLike.commentLike.member.id.eq(memberId))
                .fetchOne();

        return Optional.ofNullable(commentLike);
    }

    @Override
    public void deleteByCommentIdAndMemberId(Long memberId, Long commentId) {
        queryFactory
                .delete(commentLike)
                .where(commentLike.comment.id.eq(commentId), commentLike.member.id.eq(memberId))
                .execute();

        em.flush();
        em.clear();
    }
}
