package petPeople.pet.controller.member;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import petPeople.pet.config.auth.AuthFilterContainer;
import petPeople.pet.controller.meeting.dto.resp.MeetingPostRetrieveRespDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingRetrieveRespDto;
import petPeople.pet.controller.member.dto.req.MemberEditReqDto;
import petPeople.pet.controller.member.dto.req.MemberLocalRegisterReqDto;
import petPeople.pet.controller.member.dto.req.MemberRegisterReqDto;
import petPeople.pet.controller.member.dto.resp.*;
import petPeople.pet.controller.post.dto.resp.PostRetrieveRespDto;
import petPeople.pet.domain.meeting.repository.MeetingBookmarkRepository;
import petPeople.pet.domain.meeting.repository.MeetingMemberRepository;
import petPeople.pet.domain.meeting.repository.MeetingRepository;
import petPeople.pet.domain.meeting.service.MeetingPostService;
import petPeople.pet.domain.meeting.service.MeetingService;
import petPeople.pet.domain.meeting.service.MeetingWaitingMemberService;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.service.MemberRegisterDto;
import petPeople.pet.domain.member.service.MemberService;
import petPeople.pet.domain.notification.repository.NotificationRepository;
import petPeople.pet.domain.post.service.PostService;
import petPeople.pet.filter.MockJwtFilter;
import petPeople.pet.util.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final PostService postService;
    private final MeetingService meetingService;
    private final MeetingPostService meetingPostService;
    private final FirebaseAuth firebaseAuth;
    private final MeetingWaitingMemberService meetingWaitingMemberService;
    private final MeetingBookmarkRepository meetingBookmarkRepository;
    private final NotificationRepository notificationRepository;
    private final MeetingMemberRepository meetingMemberRepository;
    private final AuthFilterContainer authFilterContainer;

    //로컬 회원 가입
    @ApiOperation(value = "로컬 회원 가입", notes = "배포용으로 쓰이지 않습니다. 로컬에서 회원 가입 용입니다.")
    @PostMapping("/local")
    public ResponseEntity<MemberRegisterRespDto> registerLocalMember(@ApiParam(value = "로컬 회원 가입 DTO", required = true) @RequestBody MemberLocalRegisterReqDto memberLocalRegisterReqDto) {
        MemberRegisterRespDto responseDto = memberService.register(new MemberRegisterDto(memberLocalRegisterReqDto));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }
    
    //파이어베이스 회원 가입
    @PostMapping("")
    @ApiOperation(value = "파이어베이스 회원 가입", notes = "배포용 회원 가입입니다.")
    public ResponseEntity<MemberRegisterRespDto> registerMember(@ApiParam(value = "파이어베이스 토큰", required = true) @RequestHeader("Authorization") String header,
                                                                @RequestBody MemberRegisterReqDto memberRegisterReqDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(memberService.register(new MemberRegisterDto(decodeToken(header), memberRegisterReqDto)));
    }

    @GetMapping("/me")
    @ApiOperation(value = "로그인 API", notes = "파이어베이스 인증 토큰을 Header 에 넣어 로그인을 요청합니다.")
    public ResponseEntity<MemberRegisterRespDto> login(Authentication authentication) {

        Member member = getMember(authentication);
        Long countMemberPost = postService.countMemberPost(member);
        Long countMemberMeeting = meetingService.countMemberMeeting(member);
        // TODO: 2022-06-29 알림 개수

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MemberRegisterRespDto(member, countMemberPost, countMemberMeeting, 0L));
    }

    @PatchMapping("/me")
    @ApiOperation(value = "닉네임 수정 API", notes = "수정하고자 하는 회원의 토큰을 Header 에 넣고, 변경하고자 하는 nickname 과 introduce 를 요청 바디에 넣어주세요")
    public ResponseEntity editMember(Authentication authentication,
                                     @ApiParam(value = "수정하고자 하는 nickname, introduce (요청 바디)", required = true) @RequestBody MemberEditReqDto memberEditReqDto) {
        Member member = getMember(authentication);
        memberService.editNickname(member, memberEditReqDto.getNickname());
        memberService.editIntroduce(member, memberEditReqDto.getIntroduce());

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/me/posts")
    @ApiOperation(value = "회원이 작성한 게시글 조회 API", notes = "자신이 작성한 게시글을 조회합니다.")
    public ResponseEntity<Slice<PostRetrieveRespDto>> retrieveMemberPost(Authentication authentication, Pageable pageable, HttpServletRequest request) {
        String header = RequestUtil.getAuthorizationToken(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(postService.retrieveMemberPost(getMember(authentication), pageable, header));
    }
    
    //내가 가입한 모임
    @ApiOperation(value = "회원이 가입한 모임을 조회 API", notes = "자신이 개설한 모임과 가입한 모임을 조회합니다.")
    @GetMapping("/me/meetings")
    public ResponseEntity<Slice<MeetingRetrieveRespDto>> retrieveMemberMeeting(Authentication authentication, Pageable pageable) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingService.retrieveMemberMeeting(getMember(authentication), pageable));
    }

    //내 모임에 신청 대기자 확인
    @ApiOperation(value = "내 모임에 신청 대기자 조회 API", notes = "특정 모임에 가입 신청한 대기자 조회")
    @GetMapping("/me/meetings/{meetingId}")
    public ResponseEntity<List<MeetingWaitingMemberRespDto>> retrieveMeetingWaitingMember(Authentication authentication, @PathVariable Long meetingId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingWaitingMemberService.retrieveMeetingWaitingMember(getMember(authentication), meetingId));
    }
    
    //내가 가입 신청한 모임 현황
    @ApiOperation(value = "내가 가입 신청한 모임 현황 API", notes = "회원이 가입한 모임 조회")
    @GetMapping("/me/meetings/apply")
    public ResponseEntity<Slice<MeetingJoinApplyRespDto>> retrieveJoinRequestMeeting(Authentication authentication, Pageable pageable) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingWaitingMemberService.retrieveMeetingWaitingMemberApply(pageable, getMember(authentication)));
    }

    @ApiOperation(value = "회원의 게시글, 알림, 북마크 수 조회 API", notes = "회원의 게시글, 알림, 북마크 수 조회 API")
    @GetMapping("/me/info")
    public ResponseEntity<MemberCountDto> info(Authentication authentication) {

        Member member = getMember(authentication);
        Long countJoinedMemberInMeeting = meetingMemberRepository.countByMemberId(member.getId());
        Long countMeetingBookmark = meetingBookmarkRepository.countByMemberId(getMemberId(member));
        long countUnReadMemberNotifications = notificationRepository.countUnReadMemberNotifications(getMemberId(member));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MemberCountDto(countJoinedMemberInMeeting, countMeetingBookmark, countUnReadMemberNotifications));
    }

    private Long getMemberId(Member member) {
        return member.getId();
    }

    @ApiOperation(value = "회원이 작성한 모임 게시글 조회 API", notes = "회원이 작성한 모임 게시글 조회")
    @GetMapping("/me/meetings/meetingPosts")
    public ResponseEntity<Slice<MeetingPostRetrieveRespDto>> retrieveMemberMeetingPost(Authentication authentication, Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingPostService.retrieveMemberMeetingPost(getMember(authentication), pageable));
    }

    @ApiOperation(value = "회원의 게시글 좋아요 조회 API", notes = "회원의 좋아요 한 게시글 조회")
    @GetMapping("/me/likes")
    public ResponseEntity<Slice<PostRetrieveRespDto>> retrieveMemberLikePost(Authentication authentication, Pageable pageable) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(postService.retrieveMemberBookMarkPost(getMember(authentication), pageable));
    }

    @ApiOperation(value = "회원의 모임 북마크 조회 API", notes = "회원의 모임 북마크 조회")
    @GetMapping("/me/meetingBookmark")
    public ResponseEntity<Slice<MemberMeetingBookMarkRespDto>> retrieveMemberBookMarkMeeting(Authentication authentication, Pageable pageable) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingService.retrieveMemberBookMarkMeeting(getMember(authentication), pageable));
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
