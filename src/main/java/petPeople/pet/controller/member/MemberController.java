package petPeople.pet.controller.member;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import petPeople.pet.controller.meeting.dto.resp.MeetingRetrieveRespDto;
import petPeople.pet.controller.member.dto.req.MemberEditReqDto;
import petPeople.pet.controller.member.dto.req.MemberLocalRegisterReqDto;
import petPeople.pet.controller.member.dto.req.MemberRegisterReqDto;
import petPeople.pet.controller.member.dto.resp.MemberRegisterRespDto;
import petPeople.pet.controller.post.dto.resp.PostRetrieveRespDto;
import petPeople.pet.domain.meeting.service.MeetingService;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.service.MemberRegisterDto;
import petPeople.pet.domain.member.service.MemberService;
import petPeople.pet.domain.post.service.PostService;
import petPeople.pet.util.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final PostService postService;
    private final MeetingService meetingService;
    private final FirebaseAuth firebaseAuth;

    //로컬 회원 가입
    @PostMapping("/local")
    public ResponseEntity<MemberRegisterRespDto> registerLocalMember(@RequestBody MemberLocalRegisterReqDto memberLocalRegisterReqDto) {
        MemberRegisterRespDto responseDto = memberService.register(new MemberRegisterDto(memberLocalRegisterReqDto));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }
    
    //파이어베이스 회원 가입
    @PostMapping("")
    public ResponseEntity<MemberRegisterRespDto> registerMember(@RequestHeader("Authorization") String header,
                                                                @RequestBody MemberRegisterReqDto memberRegisterReqDto) {

        MemberRegisterRespDto responseDto = memberService.register(new MemberRegisterDto(decodeToken(header), memberRegisterReqDto));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @GetMapping("/me")
    public ResponseEntity<MemberRegisterRespDto> login(Authentication authentication) {
        Member member = getMember(authentication);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MemberRegisterRespDto(member));
    }

    @PatchMapping("/me")
    public ResponseEntity editMember(Authentication authentication,
                                     @RequestBody MemberEditReqDto memberEditReqDto) {
        Member member = getMember(authentication);
        memberService.editNickname(member, memberEditReqDto.getNickname());
        memberService.editIntroduce(member, memberEditReqDto.getIntroduce());

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/me/posts")
    public ResponseEntity<Slice<PostRetrieveRespDto>> retrieveMemberPost(Authentication authentication, Pageable pageable, HttpServletRequest request) {
        String header = RequestUtil.getAuthorizationToken(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(postService.retrieveMemberPost(getMember(authentication), pageable, header));
    }

    @GetMapping("/me/meetings")
    public ResponseEntity<Slice<MeetingRetrieveRespDto>> retrieveMemberMeeting(Authentication authentication, Pageable pageable) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingService.retrieveMemberMeeting(getMember(authentication), pageable));

    }


    private Member getMember(Authentication authentication) {
        return (Member) authentication.getPrincipal();
    }

    public FirebaseToken decodeToken(String header) {
        try {
            String token = RequestUtil.getAuthorizationToken(header);
            return firebaseAuth.verifyIdToken(token);
        } catch (IllegalArgumentException | FirebaseAuthException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "{\"code\":\"INVALID_TOKEN\", \"message\":\"" + e.getMessage() + "\"}");
        }
    }

}
