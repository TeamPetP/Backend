package petPeople.pet.domain.post.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import petPeople.pet.domain.post.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostCustomRepository {

    Slice<Post> findAllSlicing(Pageable pageable);
    Optional<Post> findByIdWithFetchJoinMember(Long postId);
    Slice<Post> findPostSlicingByTag(Pageable pageable, String tag);
    Slice<Post> findAllByMemberIdSlicing(Long memberId, Pageable pageable);
}
