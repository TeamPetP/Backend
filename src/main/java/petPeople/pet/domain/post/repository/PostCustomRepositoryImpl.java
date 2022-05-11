package petPeople.pet.domain.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import petPeople.pet.domain.member.entity.QMember;
import petPeople.pet.domain.post.entity.Post;
import petPeople.pet.domain.post.entity.QPost;

import java.util.List;
import java.util.Optional;

import static petPeople.pet.domain.member.entity.QMember.*;
import static petPeople.pet.domain.post.entity.QPost.*;

@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Post> findByIdWithFetchJoinMember(Long postId) {
        Post post = queryFactory
                .selectFrom(QPost.post)
                .join(QPost.post.member, member)
                .where(QPost.post.id.eq(postId))
                .fetchOne();

        return Optional.of(post);
    }

    @Override
    public Page<Post> findAllPostByIdWithFetchJoinMemberPaging(Pageable pageable) {

        List<Post> content = queryFactory
                .selectFrom(post)
                .join(post.member, member).fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdDate.desc())
                .fetch();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
