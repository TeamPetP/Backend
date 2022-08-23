package petPeople.pet.domain.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import petPeople.pet.domain.post.entity.PostLike;
import petPeople.pet.domain.post.entity.QPost;
import petPeople.pet.domain.post.entity.QPostLike;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static petPeople.pet.domain.post.entity.QPost.*;
import static petPeople.pet.domain.post.entity.QPostLike.*;
import static petPeople.pet.domain.post.entity.QPostLike.postLike;

@RequiredArgsConstructor
public class PostLikeCustomRepositoryImpl implements PostLikeCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public List<PostLike> findPostLikesByPostIds(List<Long> ids) {

        return queryFactory
                .selectFrom(postLike)
                .where(postLike.post.id.in(ids))
                .fetch();
    }

    @Override
    public Optional<PostLike> findPostLikeByPostIdAndMemberId(Long postId, Long memberId) {
        PostLike postLike = queryFactory
                .selectFrom(QPostLike.postLike)
                .where(QPostLike.postLike.post.id.eq(postId), QPostLike.postLike.member.id.eq(memberId))
                .fetchOne();

        return Optional.ofNullable(postLike);
    }

    @Override
    public Long deleteByPostId(Long postId) {
        long count = queryFactory
                .delete(postLike)
                .where(postLike.post.id.eq(postId))
                .execute();

        em.flush();
        em.clear();
        return count;
    }

    @Override
    public Long deleteByPostIdAndMemberId(Long postId, Long memberId) {
        long count = queryFactory
                .delete(postLike)
                .where(postLike.post.id.eq(postId), postLike.member.id.eq(memberId))
                .execute();

        em.flush();
        em.clear();
        return count;
    }

    @Override
    public Long countByPostId(Long postId) {
        return queryFactory
                .select(postLike.count())
                .from(postLike)
                .where(postLike.post.id.eq(postId))
                .fetchOne();
    }

    @Override
    public Slice<PostLike> findByMemberIdWithFetchJoinPost(Long memberId, Pageable pageable) {
        List<PostLike> content = queryFactory
                .select(postLike)
                .from(postLike)
                .where(postLike.member.id.eq(memberId))
                .join(postLike.post, post).fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(postLike.createdDate.desc())
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }
}
