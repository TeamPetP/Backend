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
import petPeople.pet.controller.post.dto.req.PostWriteReqDto;
import petPeople.pet.controller.post.dto.resp.PostEditRespDto;
import petPeople.pet.controller.post.dto.resp.PostRetrieveRespDto;
import petPeople.pet.controller.post.dto.resp.PostWriteRespDto;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.post.service.PostService;
import petPeople.pet.filter.MockJwtFilter;
import petPeople.pet.util.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final AuthFilterContainer authFilterContainer;

    @ApiOperation(value = "게시글 작성 API", notes = "게시글 작성을 위해 header 에 토큰을 입력해주세요")
    @PostMapping("")
    public ResponseEntity<PostWriteRespDto> writePost(Authentication authentication,
                                                      @ApiParam(value = "게시글 작성 DTO", required = true) @RequestBody PostWriteReqDto postWriteReqDto) {
        PostWriteRespDto respDto = postService.write(getMember(authentication), postWriteReqDto);

        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body(respDto);
    }

    @ApiOperation(value = "게시글 단건 조회 API", notes = "단건 조회할 게시글 postId 를 경로변수에 넣어주세요(헤더에 토큰이 있을 경우 좋아요 여부를 알려줍니다.)")
    @GetMapping("/{postId}")
    public ResponseEntity<PostRetrieveRespDto> retrievePost(@ApiParam(value = "게시글 ID", required = true) @PathVariable Long postId,
                                                            HttpServletRequest request) {
        String header = RequestUtil.getAuthorizationToken(request);

        if (authFilterContainer.getFilter() instanceof MockJwtFilter) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(postService.localRetrieveOne(postId, Optional.ofNullable(header)));
        } else {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(postService.retrieveOne(postId, Optional.ofNullable(header)));
        }
    }

    @ApiOperation(value = "게시글 전체 조회 API", notes = "게시글 전체 조회(헤더에 토큰이 있을 경우 좋아요 여부를 알려줍니다.)")
    @GetMapping("")
    public ResponseEntity<Slice<PostRetrieveRespDto>> retrieveAllPost(Pageable pageable,
                                                                      @ApiParam(value = "태그로 검색할 경우 태그 입력", required = true) @RequestParam(required = false) String tag, HttpServletRequest request) {
        String header = RequestUtil.getAuthorizationToken(request);
        if (authFilterContainer.getFilter() instanceof MockJwtFilter) {
            return ResponseEntity.ok().body(postService.localRetrieveAll(pageable, Optional.ofNullable(tag), Optional.ofNullable(header)));
        } else
            return ResponseEntity.ok().body(postService.retrieveAll(pageable, Optional.ofNullable(tag), Optional.ofNullable(header)));
    }

    @ApiOperation(value = "게시글 수정 API", notes = "header 에 토큰을 입력해주세요, 수정할 게시글 postId 를 경로변수에 넣어주세요")
    @PutMapping("/{postId}")
    public ResponseEntity<PostEditRespDto> editPost(Authentication authentication,
                                                    @ApiParam(value = "게시글 ID", required = true) @PathVariable Long postId,
                                                    @ApiParam(value = "게시글 작성, 수정 DTO", required = true) @RequestBody PostWriteReqDto postWriteReqDto) {
        PostEditRespDto respDto = postService.editPost(getMember(authentication), postId, postWriteReqDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(respDto);
    }

    @ApiOperation(value = "게시글 좋아요 조회 API", notes = "header 에 토큰을 입력해주세요, 게시글 좋아요 할 postId 를 경로변수에 넣어주세요")
    @PatchMapping("/{postId}")
    public ResponseEntity<Long> likePost(Authentication authentication,
                                         @ApiParam(value = "게시글 ID", required = true) @PathVariable Long postId) {
        Long like = postService.like(getMember(authentication), postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(like);
    }

    @ApiOperation(value = "게시글 삭제 API", notes = "header 에 토큰을 입력해주세요, 게시글 삭제할 postId 를 경로변수에 넣어주세요")
    @DeleteMapping("/{postId}")
    public ResponseEntity deletePost(Authentication authentication,
                                     @ApiParam(value = "게시글 ID", required = true) @PathVariable Long postId) {
        postService.delete(getMember(authentication), postId);

        return ResponseEntity
                .noContent()
                .build();
    }

    @ApiOperation(value = "게시글 북마크 API", notes = "header 에 토큰을 입력해주세요, 북마크할 postId 를 경로변수에 넣어주세요")
    @PatchMapping("/{postId}/bookmarks")
    public ResponseEntity bookmarkPost(Authentication authentication,
                                       @ApiParam(value = "게시글 ID", required = true) @PathVariable Long postId) {
        postService.bookmark(getMember(authentication), postId);

        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "게시글 북마크 취소 API", notes = "header 에 토큰을 입력해주세요, 북마크 취소할 postId 를 경로변수에 넣어주세요")
    @DeleteMapping("/{postId}/bookmarks")
    public ResponseEntity deleteBookmarkPost(Authentication authentication,
                                             @ApiParam(value = "게시글 ID", required = true) @PathVariable Long postId) {
        postService.deleteBookmark(getMember(authentication), postId);

        return ResponseEntity.noContent().build();
    }

    private Member getMember(Authentication authentication) {
        return (Member) authentication.getPrincipal();
    }
}
