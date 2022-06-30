package petPeople.pet.controller.meeting;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import petPeople.pet.controller.meeting.dto.req.MeetingCommentWriteReqDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingCommentRetrieveRespDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingCommentWriteRespDto;
import petPeople.pet.domain.meeting.service.MeetingCommentService;
import petPeople.pet.domain.member.entity.Member;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meetings")
public class MeetingCommentController {

    private final MeetingCommentService meetingCommentService;

    @PostMapping("/{meetingId}/meetingPosts/{meetingPostId}/meetingComments")
    @ApiOperation(value = "모임 게시글 댓글 작성 API", notes = "모임 게시글 댓글 작성과 권한 검사를 위해 header 에 토큰을 입력해주세요")
    public ResponseEntity<MeetingCommentWriteRespDto> writeMeetingComment(@PathVariable Long meetingId, @PathVariable Long meetingPostId,
                                                                          @RequestBody MeetingCommentWriteReqDto meetingCommentWriteReqDto,
                                                                          Authentication authentication) {
        Member member = getMember(authentication);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(meetingCommentService.write(meetingId, meetingPostId, meetingCommentWriteReqDto, member));
    }

    @GetMapping("/{meetingId}/meetingPosts/{meetingPostId}/meetingComments")
    @ApiOperation(value = "모임 게시글 댓글 조회 API", notes = "모임 게시글 댓글 조회와 권한 검사를 위해 header 에 토큰을 입력해주세요")
    public ResponseEntity<Slice<MeetingCommentRetrieveRespDto>> retrieveComments(@PathVariable Long meetingId, @PathVariable Long meetingPostId,
                                                                                 Authentication authentication, Pageable pageable) {

        Member member = getMember(authentication);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingCommentService.retrieveComments(meetingId, meetingPostId, member, pageable));
    }

    @PatchMapping("/{meetingId}/meetingPosts/{meetingPostId}/meetingComments/{meetingCommentId}")
    @ApiOperation(value = "모임 게시글 댓글 좋아요 API", notes = "모임 게시글 댓글 좋아요와 권한 검사를 위해 header 에 토큰을 입력해주세요(한번 더 누를 시 취소)")
    public ResponseEntity likeComment(@PathVariable Long meetingId, @PathVariable Long meetingPostId, @PathVariable Long meetingCommentId,
                                      Authentication authentication) {
        Member member = getMember(authentication);
        
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingCommentService.likeComment(meetingId, meetingPostId, meetingCommentId, member));
    }

    private Member getMember(Authentication authentication) {
        return (Member) authentication.getPrincipal();
    }

}
