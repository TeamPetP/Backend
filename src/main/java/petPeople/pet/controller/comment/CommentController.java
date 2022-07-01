package petPeople.pet.controller.comment;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    @ApiOperation(value = "댓글 수정 API", notes = "수정할 댓글 commentId를 경로변수에 넣어주세요. 댓글 작성을 위해 header에 토큰을 입력해주세요")
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentEditRespDto> editComment(Authentication authentication,
                                                          @ApiParam(value = "댓글 수정 DTO", required = true) @RequestBody CommentEditReqDto commentEditReqDto,
                                                          @ApiParam(value = "댓글 ID", required = true) @PathVariable Long commentId){
        CommentEditRespDto respDto = commentService.editComment(getMember(authentication), commentId, commentEditReqDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(respDto);
    }

    @ApiOperation(value = "댓글 삭제 API", notes = "삭제할 댓글의 commentId를 경로변수에 넣어주세요. 댓글 삭제룰 위해 header에 토큰을 입력해주세요")
    @DeleteMapping("/{commentId}")
    public ResponseEntity deleteComment(Authentication authentication,
                                        @ApiParam(value = "댓글 ID", required = true) @PathVariable Long commentId) {
        commentService.deleteComment(getMember(authentication), commentId);

        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "댓글 좋아요 API", notes = "좋아요 누를 댓글의 commentId를 경로변수에 넣어주세요. 댓글 좋아요를 위해 header에 토큰을 입력해주세요")
    @PatchMapping("/{commentId}")
    public ResponseEntity<Long> likeComment(Authentication authentication,
                                            @ApiParam(value = "댓글 ID", required = true) @PathVariable Long commentId) {
        Long likeComment = commentService.likeComment(getMember(authentication), commentId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(likeComment);
    }

    private Member getMember(Authentication authentication) {
        return (Member) authentication.getPrincipal();
    }
}
