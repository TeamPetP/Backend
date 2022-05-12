package petPeople.pet.controller.post;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import petPeople.pet.controller.post.dto.req.PostWriteReqDto;
import petPeople.pet.controller.post.dto.resp.PostEditRespDto;
import petPeople.pet.controller.post.dto.resp.PostRetrieveRespDto;
import petPeople.pet.controller.post.dto.resp.PostWriteRespDto;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.post.service.PostService;
import petPeople.pet.util.RequestUtil;

import javax.servlet.http.HttpServletRequest;

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
    public ResponseEntity<PostRetrieveRespDto> retrievePost(@PathVariable Long postId, HttpServletRequest request) {
        String header = RequestUtil.getAuthorizationToken(request);

        if (isLocalProfile()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(postService.localRetrieveOne(postId, header));
        } else {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(postService.localRetrieveOne(postId, header));
        }
    }

    @GetMapping("")
    public ResponseEntity retrieveAllPost(Pageable pageable, HttpServletRequest request) {
        String header = RequestUtil.getAuthorizationToken(request);
        if (isLocalProfile())
            return ResponseEntity.ok().body(postService.localRetrieveAll(pageable, header));
        else
            return ResponseEntity.ok().body(postService.localRetrieveAll(pageable, header));

    }

    private boolean isLocalProfile() {
        return getProfile().equals("local");
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostEditRespDto> editPost(Authentication authentication, @PathVariable Long postId, @RequestBody PostWriteReqDto postWriteReqDto) {
        PostEditRespDto respDto = postService.editPost(getMember(authentication), postId, postWriteReqDto);

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

    @PatchMapping("/{postId}/bookmarks")
    public ResponseEntity bookmarkPost(Authentication authentication, @PathVariable Long postId) {
        postService.bookmark(getMember(authentication), postId);

        return ResponseEntity.noContent().build();
    }

    private String getProfile() {
        return System.getProperty("spring.profiles.active");
    }

    private Member getMember(Authentication authentication) {
        return (Member) authentication.getPrincipal();
    }
}
