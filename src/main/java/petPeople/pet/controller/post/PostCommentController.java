package petPeople.pet.controller.post;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import petPeople.pet.controller.comment.dto.req.CommentWriteReqDto;
import petPeople.pet.controller.comment.dto.resp.CommentWriteRespDto;
import petPeople.pet.domain.comment.service.CommentService;
import petPeople.pet.domain.member.entity.Member;

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

    private Member getMember(Authentication authentication) {
        return (Member) authentication.getPrincipal();
    }
}
