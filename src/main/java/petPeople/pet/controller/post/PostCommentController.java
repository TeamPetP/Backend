package petPeople.pet.controller.post;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import petPeople.pet.config.auth.AuthFilterContainer;
import petPeople.pet.controller.comment.dto.req.CommentWriteReqDto;
import petPeople.pet.controller.comment.dto.resp.CommentWriteRespDto;
import petPeople.pet.controller.post.dto.resp.CommentRetrieveRespDto;
import petPeople.pet.controller.post.dto.resp.CommentRetrieveWithCountRespDto;
import petPeople.pet.domain.comment.service.CommentService;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.filter.MockJwtFilter;
import petPeople.pet.util.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static java.util.Optional.*;

@RestController
@RequiredArgsConstructor
public class PostCommentController {

    private final CommentService commentService;
    private final AuthFilterContainer authFilterContainer;

    @PostMapping("/posts/{postId}/comments")
    @ApiOperation(value = "댓글 작성 API", notes = "댓글 작성을 위해 header 에 토큰을 입력해주세요")
    public ResponseEntity<CommentWriteRespDto> writeComment(@ApiParam(value = "댓글 작성 DTO", required = true) @RequestBody CommentWriteReqDto commentWriteRequestDto,
                                                            @ApiParam(value = "게시글 ID", required = true) @PathVariable Long postId,
                                                            Authentication authentication) {
        CommentWriteRespDto respDto = commentService.writeComment(getMember(authentication), commentWriteRequestDto, postId, null);
        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body(respDto);
    }

    @PostMapping("/posts/{postId}/comments/{parentCommentId}")
    @ApiOperation(value = "대댓글 작성 API", notes = "대댓글 작성을 위해 header 에 토큰을 입력해주세요")
    public ResponseEntity<CommentWriteRespDto> writeChildComment(@ApiParam(value = "댓글 작성 DTO", required = true) @RequestBody CommentWriteReqDto commentWriteRequestDto,
                                                                 @ApiParam(value = "게시글 ID", required = true) @PathVariable Long postId,
                                                                 @ApiParam(value = "댓글 ID", required = true) @PathVariable Long commentId,
                                                                 Authentication authentication) {
        CommentWriteRespDto respDto = commentService.writeChildComment(getMember(authentication), commentWriteRequestDto, postId, commentId);
        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body(respDto);
    }

    @GetMapping("/posts/{postId}/comments")
    @ApiOperation(value = "댓글 전체 조회 API", notes = "댓글 조회를 위해 게시글 postId 를 경로변수에 넣어주세요(헤더에 토큰이 있을 경우 좋아요 여부를 알려줍니다.)")
    public ResponseEntity<CommentRetrieveWithCountRespDto> retrieveAllComment(@ApiParam(value = "게시글 ID", required = true) @PathVariable Long postId
            , HttpServletRequest request) {
        String header = RequestUtil.getAuthorizationToken(request);

        return ResponseEntity.ok(commentService.retrieveAll(postId, header));
    }

    private Member getMember(Authentication authentication) {
        return (Member) authentication.getPrincipal();
    }
}
