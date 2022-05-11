package petPeople.pet.domain.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import petPeople.pet.domain.post.entity.Post;

import java.util.Optional;

public interface PostCustomRepository {

    Page<Post> findAllPostByIdWithFetchJoinMemberPaging(Pageable pageable);
    Optional<Post> findByIdWithFetchJoinMember(Long postId);
}
