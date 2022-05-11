package petPeople.pet.controller.post;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import petPeople.pet.controller.post.dto.req.PostWriteReqDto;
import petPeople.pet.controller.post.dto.resp.PostRetrieveRespDto;
import petPeople.pet.controller.post.dto.resp.PostWriteRespDto;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.post.entity.Post;
import petPeople.pet.domain.post.service.PostService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping("")
    public ResponseEntity<PostWriteRespDto> writePost(Authentication authentication, @RequestBody PostWriteReqDto postWriteReqDto) {
        PostWriteRespDto respDto = postService.write(getMember(authentication), postWriteReqDto);

        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body(respDto);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostRetrieveRespDto> retrievePost(@PathVariable Long postId) {
        PostRetrieveRespDto respDto = postService.retrieveOne(postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(respDto);
    }

    @GetMapping("")
    public ResponseEntity retrieveAllPost(Pageable pageable) {
        Page<PostRetrieveRespDto> respDtoPage = postService.retrieveAll(pageable);

        return ResponseEntity.ok().body(respDtoPage);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostWriteRespDto> editPost(Authentication authentication, @PathVariable Long postId, @RequestBody PostWriteReqDto postWriteReqDto) {
        PostWriteRespDto respDto = postService.editPost(getMember(authentication), postId, postWriteReqDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(respDto);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Long> likePost(Authentication authentication, @PathVariable Long postId) {
        Long like = postService.like(getMember(authentication), postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(like);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity deletePost(Authentication authentication, @PathVariable Long postId) {
        postService.delete(getMember(authentication), postId);

        return ResponseEntity
                .noContent()
                .build();
    }

    private Member getMember(Authentication authentication) {
        return (Member) authentication.getPrincipal();
    }
}
