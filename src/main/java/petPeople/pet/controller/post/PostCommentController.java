package petPeople.pet.controller.post;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import petPeople.pet.controller.comment.dto.req.CommentWriteReqDto;
import petPeople.pet.controller.comment.dto.resp.CommentWriteRespDto;
import petPeople.pet.controller.post.dto.resp.CommentRetrieveRespDto;
import petPeople.pet.domain.comment.service.CommentService;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.util.RequestUtil;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class PostCommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentWriteRespDto> writeComment(@RequestBody CommentWriteReqDto commentWriteRequestDto,
                                                            @PathVariable Long postId,
                                                            Authentication authentication) {
        CommentWriteRespDto respDto = commentService.write(getMember(authentication), commentWriteRequestDto, postId);
        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body(respDto);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Slice<CommentRetrieveRespDto>> retrieveAllComment(@PathVariable Long postId, Pageable pageable, HttpServletRequest request) {
        String header = RequestUtil.getAuthorizationToken(request);

        return ResponseEntity.ok(commentService.retrieveAll(postId, header, pageable));
    }

    private Member getMember(Authentication authentication) {
        return (Member) authentication.getPrincipal();
    }
}
