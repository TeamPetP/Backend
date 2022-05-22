package petPeople.pet.domain.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import petPeople.pet.domain.post.entity.QTag;
import petPeople.pet.domain.post.entity.Tag;

import javax.persistence.EntityManager;

import java.util.List;

import static petPeople.pet.domain.post.entity.QTag.*;

@RequiredArgsConstructor
public class TagCustomRepositoryImpl implements TagCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public void deleteByPostId(Long postId) {
        queryFactory
                .delete(tag1)
                .where(tag1.post.id.eq(postId))
                .execute();

        em.flush();
        em.clear();
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
