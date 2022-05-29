package petPeople.pet.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import petPeople.pet.controller.comment.dto.req.CommentEditReqDto;
import petPeople.pet.controller.comment.dto.resp.CommentEditRespDto;
import petPeople.pet.domain.comment.service.CommentService;
import petPeople.pet.domain.member.entity.Member;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentEditRespDto> editComment(@RequestBody CommentEditReqDto commentEditReqDto,
                                                          Authentication authentication,
                                                          @PathVariable Long commentId){
        CommentEditRespDto respDto = commentService.editComment(getMember(authentication), commentId, commentEditReqDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(respDto);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity deleteComment(Authentication authentication,
                                        @PathVariable Long commentId) {
        commentService.deleteComment(getMember(authentication), commentId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<Long> likeComment(Authentication authentication,
                                            @PathVariable Long commentId) {
        Long likeComment = commentService.likeComment(getMember(authentication), commentId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(likeComment);
    }

    private Member getMember(Authentication authentication) {
        return (Member) authentication.getPrincipal();
    }
}
