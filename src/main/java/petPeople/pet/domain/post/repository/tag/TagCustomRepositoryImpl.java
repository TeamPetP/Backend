package petPeople.pet.domain.post.repository.tag;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import petPeople.pet.domain.post.entity.Tag;

import javax.persistence.EntityManager;

import java.util.List;

import static petPeople.pet.domain.post.entity.QTag.*;

@RequiredArgsConstructor
public class TagCustomRepositoryImpl implements TagCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public Long deleteByPostId(Long postId) {
        long count = queryFactory
                .delete(tag1)
                .where(tag1.post.id.eq(postId))
                .execute();

        em.flush();
        em.clear();

        return count;
    }

    @Override
    public List<Tag> findTagsByPostIds(List<Long> postIds) {

        return queryFactory
                .selectFrom(tag1)
                .where(tag1.post.id.in(postIds))
                .fetch();
    }

    @Override
    public List<Tag> findByPostId(Long postId) {
        return queryFactory
                .selectFrom(tag1)
                .where(tag1.post.id.eq(postId))
                .fetch();
    }
}
