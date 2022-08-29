package petPeople.pet.domain.post.repository.post_image;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import petPeople.pet.domain.post.entity.PostImage;

import javax.persistence.EntityManager;

import java.util.List;

import static petPeople.pet.domain.post.entity.QPostImage.*;

@RequiredArgsConstructor
public class PostImageCustomRepositoryImpl implements PostImageCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public void deleteByPostId(Long postId) {
        queryFactory
                .delete(postImage)
                .where(postImage.post.id.eq(postId))
                .execute();

        em.flush();
        em.clear();
    }

    @Override
    public List<PostImage> findPostImagesByPostIds(List<Long> postIds) {

        return queryFactory
                .selectFrom(postImage)
                .where(postImage.post.id.in(postIds))
                .fetch();
    }

    @Override
    public List<PostImage> findByPostId(Long postId) {
        return queryFactory
                .selectFrom(postImage)
                .where(postImage.post.id.eq(postId))
                .fetch();
    }

}
