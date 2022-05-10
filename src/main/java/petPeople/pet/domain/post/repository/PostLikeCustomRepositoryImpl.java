package petPeople.pet.domain.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import petPeople.pet.domain.post.entity.PostLike;
import petPeople.pet.domain.post.entity.QPostLike;

import javax.persistence.EntityManager;
import java.util.List;

import static petPeople.pet.domain.post.entity.QPostLike.*;

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
}
