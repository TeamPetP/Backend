package petPeople.pet.controller.meeting;

import lombok.RequiredArgsConstructor;
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
    public ResponseEntity writeMeetingPost(@RequestBody MeetingPostWriteReqDto meetingPostWriteReqDto,
                                           @PathVariable Long meetingId,
                                           Authentication authentication) {
        Member member = getMember(authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(meetingPostService.write(member, meetingPostWriteReqDto, meetingId));
    }

    private Member getMember(Authentication authentication) {
        return (Member) authentication.getPrincipal();
    }

}
