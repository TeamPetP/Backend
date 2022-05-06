package petPeople.pet.controller.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import petPeople.pet.controller.member.dto.req.MemberLocalRegisterRequestDto;
import petPeople.pet.domain.member.service.MemberRegisterDto;
import petPeople.pet.domain.member.service.MemberService;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    
    //로컬 회원 가입
    @PostMapping("/members/local")
    public ResponseEntity registerLocalMember(@RequestBody MemberLocalRegisterRequestDto memberLocalRegisterRequestDto) {
        memberService.save(new MemberRegisterDto(memberLocalRegisterRequestDto));
        return ResponseEntity.ok().build();
    }
}
