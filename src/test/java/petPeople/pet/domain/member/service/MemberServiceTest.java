package petPeople.pet.domain.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import petPeople.pet.controller.member.dto.req.MemberLocalRegisterReqDto;
import petPeople.pet.controller.member.dto.resp.MemberRegisterRespDto;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.repository.MemberRepository;
import petPeople.pet.exception.CustomException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)//테스트 클래스가 Mockito를 사용함을 의미합니다.
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    MemberService memberService;

    final String uid = "abcd";
    final String email = "abcd@daum.com";
    final String name = "성이름";
    final String nickname = "abcd";
    final String imgUrl = "https://www.balladang.com";
    final String introduce = "잘지내요 우리";
    final Integer age = 23;


    @Test
    @DisplayName("회원 조회 테스트")
    void loadByUserNameTest() throws Exception {
        //given
        Member expectedMember = createMember(uid, email, name, nickname, imgUrl, introduce);
        when(memberRepository.findByUid(any())).thenReturn(Optional.ofNullable(expectedMember));

        //when
        Member member = (Member)memberService.loadUserByUsername(uid);

        //then
        assertThat(member).isEqualTo(expectedMember);
    }

    @Test
    @DisplayName("로컬 회원 가입 테스트")
    public void memberRegisterTest() throws Exception {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);

        MemberRegisterRespDto result = new MemberRegisterRespDto(member);
        MemberLocalRegisterReqDto memberLocalRegisterReqDto = new MemberLocalRegisterReqDto(uid, name, email, nickname, imgUrl, introduce);

        when(memberRepository.save(any())).thenReturn(member);
        when(memberRepository.findByUid(any())).thenReturn(Optional.empty());

        //when
        MemberRegisterRespDto responseDto = memberService.register(new MemberRegisterDto(memberLocalRegisterReqDto));

        //then
        assertThat(responseDto).isEqualTo(result);
    }

    @Test
    @DisplayName("중복 회원 가입 테스트")
    public void duplicatedMemberRegisterTest() throws Exception {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);

        when(memberRepository.findByUid(any())).thenReturn(Optional.ofNullable(member));

        //when
        //then
        assertThrows(CustomException.class,
                () -> memberService.register(new MemberRegisterDto(uid, name, email, nickname, imgUrl, introduce, age)));
    }

    @Test
    @DisplayName("닉네임 변경 테스트")
    public void editNicknameTest() throws Exception {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);

        //when
        memberService.editNickname(member, "가나다라");

        //then
        assertThat(member.getNickname()).isEqualTo("가나다라");
    }

     @Test
     public void editIntroduceTest() throws Exception {
         //given
         Member member = createMember(uid, email, name, nickname, imgUrl, introduce);

         //when
         String introduce = "강아지죠아";
         memberService.editIntroduce(member, introduce);

         //then
         assertThat(member.getIntroduce()).isEqualTo(introduce);
     }


    private Member createMember(String uid, String email, String name, String nickname, String imgUrl, String introduce) {
        return Member.builder()
                .uid(uid)
                .email(email)
                .name(name)
                .nickname(nickname)
                .imgUrl(imgUrl)
                .introduce(introduce)
                .build();
    }
}