package petPeople.pet.controller.post;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import petPeople.pet.controller.post.dto.req.PostWriteReqDto;
import petPeople.pet.controller.post.dto.resp.PostWriteRespDto;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.post.service.PostService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping("")
    public ResponseEntity write(Authentication authentication, @RequestBody PostWriteReqDto postWriteReqDto) {
        PostWriteRespDto respDto = postService.write(getMember(authentication), postWriteReqDto);

        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body(respDto);
    }

    private Member getMember(Authentication authentication) {
        return (Member) authentication.getPrincipal();
    }
}
