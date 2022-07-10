package petPeople.pet.domain.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import petPeople.pet.controller.post.dto.resp.CommentRetrieveRespDto;
import petPeople.pet.domain.comment.entity.Comment;
import petPeople.pet.domain.comment.entity.QComment;
import petPeople.pet.domain.member.entity.QMember;

import javax.persistence.EntityManager;
import java.util.List;

import static petPeople.pet.domain.comment.entity.QComment.*;
import static petPeople.pet.domain.member.entity.QMember.*;

@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public Slice<Comment> findAllByIdWithFetchJoinMemberPaging(Long postId, Pageable pageable) {
        List<Comment> commentList = queryFactory
                .selectFrom(comment)
                .join(comment.member, member).fetchJoin()
                .where(comment.post.id.eq(postId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(comment.createdDate.asc())
                .fetch();

        boolean hasNext = false;
        if (commentList.size() > pageable.getPageSize()) {
            commentList.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl(commentList, pageable, hasNext);
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
}
