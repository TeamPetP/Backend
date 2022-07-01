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
import petPeople.pet.controller.meeting.dto.req.MeetingCreateReqDto;
import petPeople.pet.controller.meeting.dto.req.MeetingEditReqDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingCreateRespDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingEditRespDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingImageRetrieveRespDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingRetrieveRespDto;
import petPeople.pet.controller.post.model.MeetingParameter;
import petPeople.pet.domain.meeting.entity.MeetingImage;
import petPeople.pet.domain.meeting.service.MeetingService;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.filter.MockJwtFilter;
import petPeople.pet.util.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meetings")
public class MeetingController {

    private final MeetingService meetingService;
    private final AuthFilterContainer authFilterContainer;

    @PostMapping("")
    @ApiOperation(value = "모임 생성 API", notes = "모임 생성을 위해 header 에 토큰을 입력해주세요")
    public ResponseEntity<MeetingCreateRespDto> createMeeting(Authentication authentication,
                                                              @ApiParam(value = "모임 개설 DTO", required = true) @RequestBody MeetingCreateReqDto meetingCreateReqDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(meetingService.create(getMember(authentication), meetingCreateReqDto));
    }

    @GetMapping("/{meetingId}")
    @ApiOperation(value = "모임 단건 조회 API", notes = "단건 조회할 모임 ID를 경로 변수에 넣어주세요(헤더에 토큰이 있을 경우 가입 여부를 알립니다.)")
    public ResponseEntity<MeetingRetrieveRespDto> retrieveMeeting(@ApiParam(value = "모임 ID", required = true) @PathVariable Long meetingId,
                                                                  HttpServletRequest request) {
        String header = RequestUtil.getAuthorizationToken(request);

        if (authFilterContainer.getFilter() instanceof MockJwtFilter) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(meetingService.localRetrieveOne(meetingId, Optional.ofNullable(header)));
        } else {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(meetingService.retrieveOne(meetingId, Optional.ofNullable(header)));
        }
    }

    @GetMapping("")
    @ApiOperation(value = "모임 전체 조회 API", notes = "모임을 전체 조회합니다.(헤더에 토큰이 있을 경우 가입 여부를 알립니다.)")
    public ResponseEntity<Slice<MeetingRetrieveRespDto>> retrieveAllMeeting(@ModelAttribute MeetingParameter meetingParameter,
                                                                            Pageable pageable, HttpServletRequest request) {
        String header = RequestUtil.getAuthorizationToken(request);
        if (authFilterContainer.getFilter() instanceof MockJwtFilter) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(meetingService.localRetrieveAll(pageable, Optional.ofNullable(header), meetingParameter));
        } else {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(meetingService.retrieveAll(pageable, Optional.ofNullable(header), meetingParameter));
        }
    }

    @PutMapping("/{meetingId}")
    @ApiOperation(value = "모임 수정 API", notes = "모임을 수정을 위해 meetingId 를 경로변수에 넣어주세요. (헤더에 토큰을 입력해주세요.)")
    public ResponseEntity<MeetingEditRespDto> editMeeting(Authentication authentication,
                                                          @ApiParam(value = "모임 ID", required = true) @PathVariable Long meetingId,
                                                          @ApiParam(value = "모임 수정 DTO", required = true) @RequestBody MeetingEditReqDto meetingEditReqDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingService.edit(getMember(authentication), meetingId, meetingEditReqDto));
    }

    @PostMapping("/{meetingId}")
    @ApiOperation(value = "모임 가입 요청 API", notes = "모임을 가입 요청을 위해 meetingId 를 경로변수에 넣어주세요. (헤더에 토큰을 입력해주세요.)")
    public ResponseEntity joinMeeting(Authentication authentication,
                                      @ApiParam(value = "모임 ID", required = true) @PathVariable Long meetingId) {
        meetingService.joinRequest(getMember(authentication), meetingId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{meetingId}")
    @ApiOperation(value = "모임 탈퇴 API", notes = "모임을 탈퇴를 위해 meetingId 를 경로변수에 넣어주세요. (헤더에 토큰을 입력해주세요.)")
    public ResponseEntity resignMeeting(@ApiParam(value = "모임 ID", required = true) @PathVariable Long meetingId,
                                        Authentication authentication) {
        Member member = getMember(authentication);
        meetingService.resign(meetingId, member);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{meetingId}/meetingBookmarks")
    @ApiOperation(value = "관심 모임 북마크 등록 API", notes = "북마크 등록를 위해 meetingId 를 경로변수에 넣어주세요. (헤더에 토큰을 입력해주세요.)")
    public ResponseEntity bookMeeting(Authentication authentication,
                                      @ApiParam(value = "모임 ID", required = true) @PathVariable Long meetingId) {
        getMember(authentication);
        meetingService.bookmark(getMember(authentication), meetingId);

        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "관심 모임 북마크 취소 API", notes = "header 에 토큰을 입력해주세요, 북마크 취소할 meetingId 를 경로변수에 넣어주세요")
    @DeleteMapping("/{meetingId}/meetingBookmarks")
    public ResponseEntity deleteBookmarkMeeting(Authentication authentication,
                                             @ApiParam(value = "게시글 ID", required = true) @PathVariable Long meetingId) {
        meetingService.deleteBookmark(getMember(authentication), meetingId);

        return ResponseEntity.noContent().build();
    }

    // TODO: 2022-06-28 모임 이미지가 아닌 모임 게시글에 있는 모든 사진

    @GetMapping("/{meetingId}/images")
    public ResponseEntity<List<MeetingImageRetrieveRespDto>> retrieveAllMeetingImage(@PathVariable Long meetingId) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingService.retrieveAllImage(meetingId));
    }

    @PatchMapping("/{meetingId}/members/{memberId}/approve")
    @ApiOperation(value = "모임 가입 요청 승인 API", notes = "모임을 가입 요청 위해 meetingId 와 memberId 를 경로변수에 넣어주세요. (헤더에 토큰을 입력해주세요.)")
    public ResponseEntity approveJoin(Authentication authentication,
                                      @ApiParam(value = "모임 ID", required = true) @PathVariable Long meetingId,
                                      @ApiParam(value = "회원 ID", required = true) @PathVariable Long memberId) {
        meetingService.approve(getMember(authentication), meetingId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{meetingId}/members/{memberId}/decline")
    @ApiOperation(value = "모임 가입 요청 거절 API", notes = "모임을 가입 요청 거부를 위해 meetingId 와 memberId 를 경로변수에 넣어주세요. (헤더에 토큰을 입력해주세요.)")
    public ResponseEntity declineJoin(Authentication authentication,
                                      @ApiParam(value = "모임 ID", required = true) @PathVariable Long meetingId,
                                      @ApiParam(value = "회원 ID", required = true) @PathVariable Long memberId) {
        meetingService.decline(getMember(authentication), meetingId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{meetingId}/members/{memberId}/cancel")
    @ApiOperation(value = "모임 가입 신청 중 취소 API", notes = "모임을 가입 신청 취소를 위해 meetingId 와 memberId 를 경로변수에 넣어주세요. (헤더에 토큰을 입력해주세요.)")
    public ResponseEntity cancelJoinRequest(@PathVariable Long meetingId, @PathVariable Long memberId, Authentication authentication) {

        meetingService.cancelJoinRequest(meetingId, memberId, getMember(authentication));
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "모임 회원 추방 API", notes = "모임에 가입한 회원의 추방을 위해 meetingId 와 memberId 를 경로변수에 넣어주세요. (헤더에 토큰을 입력해주세요.)")
    @PatchMapping("/{meetingId}/members/{memberId}/expel")
    public ResponseEntity expelMeetingMember(@PathVariable Long meetingId, @PathVariable Long memberId, Authentication authentication) {
        meetingService.expelMeetingMember(meetingId, memberId, getMember(authentication));
        return ResponseEntity
                .noContent()
                .build();
    }

    private Member getMember(Authentication authentication) {
        return (Member) authentication.getPrincipal();
    }

}