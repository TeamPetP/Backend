package petPeople.pet.controller.post;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import petPeople.pet.controller.post.dto.req.PostWriteReqDto;
import petPeople.pet.controller.post.dto.resp.PostRetrieveRespDto;
import petPeople.pet.controller.post.dto.resp.PostWriteRespDto;
import petPeople.pet.domain.member.entity.Member;
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
        PostRetrieveRespDto postRetrieveRespDto = postService.retrieveOne(postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(postRetrieveRespDto);
    }

    @PutMapping("/{postId}")
    public ResponseEntity editPost(Authentication authentication, @PathVariable Long postId, @RequestBody PostWriteReqDto postWriteReqDto) {
        Member member = getMember(authentication);
        PostWriteRespDto postWriteRespDto = postService.editPost(member, postId, postWriteReqDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(postWriteRespDto);
    }

    private Member getMember(Authentication authentication) {
        return (Member) authentication.getPrincipal();
    }
}
