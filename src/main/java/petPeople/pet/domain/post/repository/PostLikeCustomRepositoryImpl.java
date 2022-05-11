package petPeople.pet.domain.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import petPeople.pet.domain.post.entity.PostLike;
import petPeople.pet.domain.post.entity.QPostLike;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

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
}