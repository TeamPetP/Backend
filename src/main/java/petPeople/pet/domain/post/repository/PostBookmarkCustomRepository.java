package petPeople.pet.domain.post.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.post.entity.PostBookmark;

import java.util.Optional;

public interface PostBookmarkCustomRepository {
    Optional<PostBookmark> findByMemberIdAndPostId(Long memberId, Long postId);
    void deleteByMemberIdAndPostId(Long memberId, Long postId);
    Slice<PostBookmark> findByMemberIdWithFetchJoinPost(Long memberId, Pageable pageable);
}
