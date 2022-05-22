package petPeople.pet.controller.meeting;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import petPeople.pet.controller.meeting.dto.req.MeetingCreateReqDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingCreateRespDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingRetrieveRespDto;
import petPeople.pet.domain.meeting.entity.Meeting;
import petPeople.pet.domain.meeting.service.MeetingService;
import petPeople.pet.domain.member.entity.Member;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping("")
    public ResponseEntity<MeetingCreateRespDto> createMeeting(Authentication authentication, @RequestBody MeetingCreateReqDto meetingCreateReqDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(meetingService.create(getMember(authentication), meetingCreateReqDto));
    }

    @GetMapping("/{meetingId}")
    public ResponseEntity<MeetingRetrieveRespDto> retrieveMeeting(@PathVariable Long meetingId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingService.retrieveOne(meetingId));
    }

    @GetMapping("")
    public ResponseEntity retrieveAllMeeting(Pageable pageable) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingService.retrieveAll(pageable));
    }

    private Member getMember(Authentication authentication) {
        return (Member) authentication.getPrincipal();
    }

}
