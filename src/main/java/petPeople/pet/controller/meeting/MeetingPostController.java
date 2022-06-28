package petPeople.pet.controller.meeting;

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
import petPeople.pet.controller.meeting.dto.req.MeetingPostWriteReqDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingPostRetrieveRespDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingPostWriteRespDto;
import petPeople.pet.domain.meeting.service.MeetingPostService;
import petPeople.pet.domain.member.entity.Member;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meetings")
public class MeetingPostController {

    private final MeetingPostService meetingPostService;

    @PostMapping("/{meetingId}/meetingPosts")
    @ApiOperation(value = "모임 게시글 작성 API", notes = "모임 게시글 작성을 위해 header 에 토큰을 입력해주세요")
    public ResponseEntity<MeetingPostWriteRespDto> writeMeetingPost(@ApiParam(value = "모임 게시글 작성 DTO", required = true) @RequestBody MeetingPostWriteReqDto meetingPostWriteReqDto,
                                                                    @ApiParam(value = "모임 ID", required = true) @PathVariable Long meetingId,
                                           Authentication authentication) {
        Member member = getMember(authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(meetingPostService.write(member, meetingPostWriteReqDto, meetingId));
    }

    @GetMapping("/{meetingId}/meetingPosts/{meetingPostId}")
    @ApiOperation(value = "모임 게시글 단건 조회 API", notes = "모임 게시글 조회 권한 검사를 위해 header 에 토큰을 입력해주세요")
    public ResponseEntity<MeetingPostRetrieveRespDto> retrieveOneMeetingPost(@ApiParam(value = "모임 ID", required = true) @PathVariable Long meetingId,
                                                                             @ApiParam(value = "모임 게시글 ID", required = true) @PathVariable Long meetingPostId, Authentication authentication) {
        Member member = getMember(authentication);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingPostService.retrieveOne(meetingId, meetingPostId, member));
    }

    @GetMapping("/{meetingId}/meetingPosts")
    @ApiOperation(value = "모임 게시글 전체 조회 API", notes = "모임 게시글 전체 조회 권한 검사를 위해 header 에 토큰을 입력해주세요")
    public ResponseEntity<Slice<MeetingPostRetrieveRespDto>> retrieveAllMeetingPost(@ApiParam(value = "모임 ID", required = true) @PathVariable Long meetingId,
                                                                                    Pageable pageable,
                                                                                    Authentication authentication) {
        Member member = getMember(authentication);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingPostService.retrieveAll(meetingId, pageable, member));
    }

    @PutMapping("/{meetingId}/meetingPosts/{meetingPostId}")
    @ApiOperation(value = "모임 게시글 수정 API", notes = "모임 게시글 수정과 권한 검사를 위해 header 에 토큰을 입력해주세요")
    public ResponseEntity<MeetingPostWriteRespDto> editMeetingPost(@ApiParam(value = "모임 ID", required = true) @PathVariable Long meetingId,
                                                                   @ApiParam(value = "모임 게시글 ID", required = true) @PathVariable Long meetingPostId,
                                                                   @ApiParam(value = "모임 게시글 수정/작성 DTO", required = true) @RequestBody MeetingPostWriteReqDto meetingPostWriteReqDto, Authentication authentication) {
        Member member = getMember(authentication);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingPostService.edit(meetingId, meetingPostId, meetingPostWriteReqDto, member));
    }

    @PatchMapping("/{meetingId}/meetingPosts/{meetingPostId}")
    @ApiOperation(value = "모임 게시글 좋아요 API", notes = "모임 게시글 좋아요와 권한 검사를 위해 header 에 토큰을 입력해주세요(한번 더 누를 시 취소)")
    public ResponseEntity<Long> likeMeetingPost(@ApiParam(value = "모임 ID", required = true) @PathVariable Long meetingId,
                                                @ApiParam(value = "모임 게시글 ID", required = true) @PathVariable Long meetingPostId,
                                                Authentication authentication) {

        Member member = getMember(authentication);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingPostService.like(meetingId, meetingPostId, member));
    }

    @DeleteMapping("/{meetingId}/meetingPosts/{meetingPostId}")
    @ApiOperation(value = "모임 게시글 삭제 API", notes = "모임 게시글 삭제와 권한 검사를 위해 header 에 토큰을 입력해주세요")
    public ResponseEntity deleteMeetingPost(@ApiParam(value = "모임 ID", required = true) @PathVariable Long meetingId,
                                            @ApiParam(value = "모임 게시글 ID", required = true) @PathVariable Long meetingPostId,
                                            Authentication authentication) {
        Member member = getMember(authentication);

        meetingPostService.delete(meetingId, meetingPostId, member);
        return ResponseEntity.noContent().build();
    }

    private Member getMember(Authentication authentication) {
        return (Member) authentication.getPrincipal();
    }

}
