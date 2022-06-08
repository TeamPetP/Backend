package petPeople.pet.controller.meeting;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import petPeople.pet.controller.meeting.dto.req.MeetingPostWriteReqDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingPostWriteRespDto;
import petPeople.pet.domain.meeting.service.MeetingPostService;
import petPeople.pet.domain.member.entity.Member;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meetings")
public class MeetingPostController {

    private final MeetingPostService meetingPostService;

    @PostMapping("/{meetingId}/posts")
    public ResponseEntity<MeetingPostWriteRespDto> writeMeetingPost(@RequestBody MeetingPostWriteReqDto meetingPostWriteReqDto,
                                           @PathVariable Long meetingId,
                                           Authentication authentication) {
        Member member = getMember(authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(meetingPostService.write(member, meetingPostWriteReqDto, meetingId));
    }

    @GetMapping("/{meetingId}/posts/{meetingPostId}")
    public ResponseEntity<MeetingPostWriteRespDto> retrieveOneMeetingPost(@PathVariable Long meetingId, @PathVariable Long meetingPostId, Authentication authentication) {
        Member member = getMember(authentication);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingPostService.retrieveOne(meetingId, meetingPostId, member));
    }

    @GetMapping("/{meetingId}/posts")
    public ResponseEntity<Slice<MeetingPostWriteRespDto>> retrieveAllMeetingPost(@PathVariable Long meetingId, Pageable pageable, Authentication authentication) {
        Member member = getMember(authentication);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingPostService.retrieveAll(meetingId, pageable, member));
    }

    private Member getMember(Authentication authentication) {
        return (Member) authentication.getPrincipal();
    }

}
