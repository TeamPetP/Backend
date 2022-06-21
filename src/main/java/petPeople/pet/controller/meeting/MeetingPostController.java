package petPeople.pet.controller.meeting;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import petPeople.pet.config.auth.AuthFilterContainer;
import petPeople.pet.controller.meeting.dto.req.MeetingPostWriteReqDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingPostWriteRespDto;
import petPeople.pet.controller.post.dto.resp.PostEditRespDto;
import petPeople.pet.domain.meeting.service.MeetingPostService;
import petPeople.pet.domain.member.entity.Member;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meetings")
public class MeetingPostController {

    private final MeetingPostService meetingPostService;
    private final AuthFilterContainer authFilterContainer;

    @PostMapping("/{meetingId}/meetingPosts")
    public ResponseEntity<MeetingPostWriteRespDto> writeMeetingPost(@RequestBody MeetingPostWriteReqDto meetingPostWriteReqDto,
                                           @PathVariable Long meetingId,
                                           Authentication authentication) {
        Member member = getMember(authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(meetingPostService.write(member, meetingPostWriteReqDto, meetingId));
    }

    @GetMapping("/{meetingId}/meetingPosts/{meetingPostId}")
    public ResponseEntity<MeetingPostWriteRespDto> retrieveOneMeetingPost(@PathVariable Long meetingId, @PathVariable Long meetingPostId, Authentication authentication) {
        Member member = getMember(authentication);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingPostService.retrieveOne(meetingId, meetingPostId, member));
    }

    @GetMapping("/{meetingId}/meetingPosts")
    public ResponseEntity<Slice<MeetingPostWriteRespDto>> retrieveAllMeetingPost(@PathVariable Long meetingId, Pageable pageable, Authentication authentication) {
        Member member = getMember(authentication);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingPostService.retrieveAll(meetingId, pageable, member));
    }

    @PutMapping("/{meetingId}/meetingPosts/{meetingPostId}")
    public ResponseEntity<MeetingPostWriteRespDto> editMeetingPost(@PathVariable Long meetingId, @PathVariable Long meetingPostId,
                                                                           @RequestBody MeetingPostWriteReqDto meetingPostWriteReqDto, Authentication authentication) {
        Member member = getMember(authentication);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingPostService.edit(meetingId, meetingPostId, meetingPostWriteReqDto, member));
    }

    @PatchMapping("/{meetingId}/meetingPosts/{meetingPostId}")
    public ResponseEntity likeMeetingPost(@PathVariable Long meetingId, @PathVariable Long meetingPostId, Authentication authentication) {

        Member member = getMember(authentication);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingPostService.like(meetingId, meetingPostId, member));
    }

//    @DeleteMapping("/{meetingId}/meetingPosts/{meetingPostId}")
//    public ResponseEntity deleteMeetingPost(@PathVariable Long meetingId, @PathVariable Long meetingPostId, Authentication authentication) {
//        Member member = getMember(authentication);
//
//        meetingPostService.delete(meetingId, meetingPostId, member);
//        return ResponseEntity.noContent().build();
//    }

    private Member getMember(Authentication authentication) {
        return (Member) authentication.getPrincipal();
    }

}
