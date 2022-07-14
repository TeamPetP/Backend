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
import petPeople.pet.domain.post.entity.QPost;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static petPeople.pet.domain.comment.entity.QComment.*;
import static petPeople.pet.domain.member.entity.QMember.*;
import static petPeople.pet.domain.post.entity.QPost.post;

@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public Slice<Comment> findAllByIdWithFetchJoinMemberPaging(Long postId, Pageable pageable) {
        List<Comment> commentParentList = queryFactory
                .selectFrom(comment)
                .join(comment.post, post).fetchJoin()
                .join(comment.member, member).fetchJoin()
                .where(comment.post.id.eq(postId), comment.parent.isNull())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(comment.createdDate.asc())
                .fetch();

        List<Comment> commentChildrenList = queryFactory
                .selectFrom(comment)
                .join(comment.member, member).fetchJoin()
                .join(comment.post, post).fetchJoin()
                .where(comment.post.id.eq(postId), comment.parent.isNotNull())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(comment.createdDate.asc())
                .fetch();

        commentParentList
                .forEach(parent -> {
                    parent.setChild(commentChildrenList.stream()
                            .filter(child -> child.getParent().getId().equals(parent.getId()))
                            .collect(Collectors.toList()));
                });


        boolean hasNext = false;
        if (commentParentList.size() > pageable.getPageSize()) {
            commentParentList.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl(commentParentList, pageable, hasNext);
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
