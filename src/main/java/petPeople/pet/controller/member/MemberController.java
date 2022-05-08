package petPeople.pet.controller.member;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import petPeople.pet.controller.member.dto.req.MemberLocalRegisterRequestDto;
import petPeople.pet.controller.member.dto.req.MemberRegisterRequestDto;
import petPeople.pet.controller.member.dto.resp.MemberRegisterResponseDto;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.service.MemberRegisterDto;
import petPeople.pet.domain.member.service.MemberService;
import petPeople.pet.util.RequestUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final FirebaseAuth firebaseAuth;

    //로컬 회원 가입
    @PostMapping("/local")
    public ResponseEntity<MemberRegisterResponseDto> registerLocalMember(@RequestBody MemberLocalRegisterRequestDto memberLocalRegisterRequestDto) {
        MemberRegisterResponseDto responseDto = memberService.register(new MemberRegisterDto(memberLocalRegisterRequestDto));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }
    
    //파이어베이스 회원 가입
    @PostMapping("")
    public ResponseEntity<MemberRegisterResponseDto> registerMember(@RequestHeader("Authorization") String header,
                                         @RequestBody MemberRegisterRequestDto memberRegisterRequestDto) {

        MemberRegisterResponseDto responseDto = memberService.register(new MemberRegisterDto(decodeToken(header), memberRegisterRequestDto));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @GetMapping("/me")
    public ResponseEntity<MemberRegisterResponseDto> login(Authentication authentication) {
        Member member = ((Member) authentication.getPrincipal());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MemberRegisterResponseDto(member));
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
