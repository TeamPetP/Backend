package petPeople.pet.domain.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import petPeople.pet.domain.post.entity.Post;

import java.util.Optional;

public interface PostCustomRepository {

    Slice<Post> findAllPostSlicing(Pageable pageable);
    Optional<Post> findByIdWithFetchJoinMember(Long postId);
    Slice<Post> findAllPostSlicingByTag(Pageable pageable, String tag);
}
