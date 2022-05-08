package petPeople.pet.domain.member.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import petPeople.pet.controller.member.dto.req.MemberLocalRegisterRequestDto;
import petPeople.pet.controller.member.dto.resp.MemberRegisterResponseDto;
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

    Member registerMember;
    Member member;

    private static final Long ID = 1L;
    private static final String UID = "abcd";
    private static final String EMAIL = "abcd@daum.com";
    private static final String NAME = "성이름";
    private static final String NICKNAME = "abcd";
    private static final String IMG_URL = "https://www.balladang.com";
    private static final String INTRODUCE = "잘지내요 우리";

    @BeforeEach
    void before() {
        registerMember = createRegisterMember();
        member = createMember();
    }

    private Member createRegisterMember() {
        return Member.builder()
                .id(ID)
                .uid(UID)
                .email(EMAIL)
                .name(NAME)
                .nickname(NICKNAME)
                .imgUrl(IMG_URL)
                .introduce(INTRODUCE)
                .build();
    }

    private Member createMember() {
        return Member.builder()
                .uid(UID)
                .email(EMAIL)
                .name(NAME)
                .nickname(NICKNAME)
                .imgUrl(IMG_URL)
                .introduce(INTRODUCE)
                .build();
    }

    @Test
    @DisplayName("회원 조회 테스트")
    void loadByUserNameTest() throws Exception {
        //given
        when(memberRepository.findByUid(UID)).thenReturn(Optional.ofNullable(registerMember));

        //when
        Member member = (Member)memberService.loadUserByUsername(UID);

        //then
        assertThat(member).isEqualTo(this.registerMember);
    }

    @Test
    @DisplayName("로컬 회원 가입 테스트")
    public void memberRegisterTest() throws Exception {
        //given
        MemberRegisterResponseDto result = new MemberRegisterResponseDto(registerMember);
        MemberLocalRegisterRequestDto memberLocalRegisterRequestDto = new MemberLocalRegisterRequestDto(UID, NAME, EMAIL, NICKNAME, IMG_URL, INTRODUCE);

        when(memberRepository.save(any())).thenReturn(registerMember);
        when(memberRepository.findByUid(any())).thenReturn(Optional.empty());

        //when
        MemberRegisterResponseDto responseDto = memberService.register(new MemberRegisterDto(memberLocalRegisterRequestDto));

        //then
        assertThat(responseDto).isEqualTo(result);
    }

    @Test
    @DisplayName("중복 회원 가입 테스트")
    public void duplicatedMemberRegisterTest() throws Exception {
        //given
        when(memberRepository.findByUid(any())).thenReturn(Optional.ofNullable(member));

        //when
        //then
        assertThrows(CustomException.class,
                () -> memberService.register(new MemberRegisterDto(UID, NAME, EMAIL, NICKNAME, IMG_URL, INTRODUCE)));
    }
}