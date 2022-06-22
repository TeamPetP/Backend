package petPeople.pet.controller.meeting;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import petPeople.pet.controller.meeting.dto.req.MeetingCommentWriteReqDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingCommentWriteRespDto;
import petPeople.pet.domain.meeting.service.MeetingCommentService;
import petPeople.pet.domain.member.entity.Member;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meetings")
public class MeetingCommentController {

    private final MeetingCommentService meetingCommentService;

    @PostMapping("/{meetingId}/meetingPosts/{meetingPostId}/meetingComments")
    public ResponseEntity<MeetingCommentWriteRespDto> writeMeetingComment(@PathVariable Long meetingId, @PathVariable Long meetingPostId,
                                                                          @RequestBody MeetingCommentWriteReqDto meetingCommentWriteReqDto,
                                                                          Authentication authentication) {

        Member member = getMember(authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(meetingCommentService.write(meetingId, meetingPostId, meetingCommentWriteReqDto, member));
    }

    private Member getMember(Authentication authentication) {
        return (Member) authentication.getPrincipal();
    }

}
