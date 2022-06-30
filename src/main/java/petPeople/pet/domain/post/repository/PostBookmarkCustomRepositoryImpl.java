package petPeople.pet.domain.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import petPeople.pet.domain.post.entity.PostBookmark;
import petPeople.pet.domain.post.entity.QPost;
import petPeople.pet.domain.post.entity.QPostBookmark;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static petPeople.pet.domain.post.entity.QPost.*;
import static petPeople.pet.domain.post.entity.QPostBookmark.postBookmark;

@RequiredArgsConstructor
public class PostBookmarkCustomRepositoryImpl implements PostBookmarkCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public Optional<PostBookmark> findByMemberIdAndPostId(Long memberId, Long postId) {

        PostBookmark postBookmark = queryFactory
                .selectFrom(QPostBookmark.postBookmark)
                .where(QPostBookmark.postBookmark.member.id.eq(memberId), QPostBookmark.postBookmark.post.id.eq(postId))
                .fetchOne();

        return Optional.ofNullable(postBookmark);
    }

    @Override
    public void deleteByMemberIdAndPostId(Long memberId, Long postId) {
        queryFactory
                .delete(postBookmark)
                .where(postBookmark.member.id.eq(memberId), postBookmark.post.id.eq(postId))
                .execute();

        em.flush();
        em.clear();
    }

    @Override
    public Slice<PostBookmark> findByMemberIdWithFetchJoinPost(Long memberId, Pageable pageable) {

        List<PostBookmark> content = queryFactory
                .select(postBookmark)
                .from(postBookmark)
                .join(postBookmark.post, post).fetchJoin()
                .where(postBookmark.member.id.eq(memberId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(postBookmark.createdDate.desc())
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }
}
