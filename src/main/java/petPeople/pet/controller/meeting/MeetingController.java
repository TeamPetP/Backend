package petPeople.pet.controller.meeting;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<MeetingCreateRespDto> createMeeting(Authentication authentication, @RequestBody MeetingCreateReqDto meetingCreateReqDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(meetingService.create(getMember(authentication), meetingCreateReqDto));
    }

    @GetMapping("/{meetingId}")
    public ResponseEntity<MeetingRetrieveRespDto> retrieveMeeting(@PathVariable Long meetingId, HttpServletRequest request) {
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
    public ResponseEntity retrieveAllMeeting(Pageable pageable, HttpServletRequest request) {
        String header = RequestUtil.getAuthorizationToken(request);

        if (authFilterContainer.getFilter() instanceof MockJwtFilter) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(meetingService.localRetrieveAll(pageable, Optional.ofNullable(header)));
        } else {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(meetingService.retrieveAll(pageable, Optional.ofNullable(header)));
        }
    }

    @PutMapping("/{meetingId}")
    public ResponseEntity<MeetingEditRespDto> editMeeting(Authentication authentication, @PathVariable Long meetingId, @RequestBody MeetingEditReqDto meetingEditReqDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingService.edit(getMember(authentication), meetingId, meetingEditReqDto));
    }

    @PostMapping("/{meetingId}")
    public ResponseEntity joinMeeting(Authentication authentication, @PathVariable Long meetingId) {
        meetingService.joinRequest(getMember(authentication), meetingId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{meetingId}")
    public ResponseEntity resignMeeting(@PathVariable Long meetingId, Authentication authentication) {
        Member member = getMember(authentication);
        meetingService.resign(meetingId, member);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{meetingId}/members/{memberId}/approve")
    public ResponseEntity approveJoin(Authentication authentication, @PathVariable Long meetingId, @PathVariable Long memberId) {
        meetingService.approve(getMember(authentication), meetingId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{meetingId}/members/{memberId}/decline")
    public ResponseEntity declineJoin(Authentication authentication, @PathVariable Long meetingId, @PathVariable Long memberId) {
        meetingService.decline(getMember(authentication), meetingId, memberId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{meetingId}/images")
    public ResponseEntity<List<MeetingImageRetrieveRespDto>> retrieveAllMeetingImage(@PathVariable Long meetingId) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingService.retrieveAllImage(meetingId));
    }

    private Member getMember(Authentication authentication) {
        return (Member) authentication.getPrincipal();
    }

}
