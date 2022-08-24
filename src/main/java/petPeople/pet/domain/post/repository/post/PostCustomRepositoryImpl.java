package petPeople.pet.domain.post.repository.post;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import petPeople.pet.domain.post.entity.Post;

import java.util.List;
import java.util.Optional;

import static petPeople.pet.domain.member.entity.QMember.*;
import static petPeople.pet.domain.post.entity.QPost.*;
import static petPeople.pet.domain.post.entity.QTag.*;

@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Post> findByIdWithFetchJoinMember(Long postId) {
        Post content = queryFactory
                .selectFrom(post)
                .join(post.member, member)
                .where(post.id.eq(postId))
                .fetchOne();

        return Optional.ofNullable(content);
    }

    @Override
    public Slice<Post> findAllSlicing(Pageable pageable) {

        List<Post> content = queryFactory
                .selectFrom(post)
                .join(post.member, member).fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(post.createdDate.desc())
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<Post> findPostSlicingByTag(Pageable pageable, String tag) {
        List<Post> content = queryFactory
                .select(post)
                .from(tag1)
                .join(tag1.post, post)
                .join(post.member, member).fetchJoin()
                .where(tag1.tag.contains(tag))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .groupBy(post.id)
                .orderBy(post.createdDate.desc())
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<Post> findAllByMemberIdSlicing(Long memberId, Pageable pageable) {
        List<Post> content = queryFactory
                .select(post)
                .from(post)
                .where(post.member.id.eq(memberId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(post.createdDate.desc())
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Long countByMemberId(Long memberId) {
        return queryFactory
                .select(post.count())
                .from(post)
                .where(post.member.id.eq(memberId))
                .fetchOne();
    }
}
