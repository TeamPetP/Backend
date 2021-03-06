package petPeople.pet.domain.member.service;

import org.junit.jupiter.api.BeforeEach;
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

    Member member;

    private static final Long ID = 1L;
    private static final String UID = "abcd";
    private static final String EMAIL = "abcd@daum.com";
    private static final String NAME = "성이름";
    private static final String NICKNAME = "abcd";
    private static final String IMG_URL = "https://www.balladang.com";
    private static final String INTRODUCE = "잘지내요 우리";
    private static final Integer AGE = 23;

    @BeforeEach
    void before() {
        member = createMember();
    }

    private Member createMember() {
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

    @Test
    @DisplayName("회원 조회 테스트")
    void loadByUserNameTest() throws Exception {
        //given
        when(memberRepository.findByUid(any())).thenReturn(Optional.ofNullable(member));

        //when
        Member member = (Member)memberService.loadUserByUsername(UID);

        //then
        assertThat(member).isEqualTo(this.member);
    }

    @Test
    @DisplayName("로컬 회원 가입 테스트")
    public void memberRegisterTest() throws Exception {
        //given
        MemberRegisterRespDto result = new MemberRegisterRespDto(member);
        MemberLocalRegisterReqDto memberLocalRegisterReqDto = new MemberLocalRegisterReqDto(UID, NAME, EMAIL, NICKNAME, IMG_URL, INTRODUCE);

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
        when(memberRepository.findByUid(any())).thenReturn(Optional.ofNullable(member));

        //when
        //then
        assertThrows(CustomException.class,
                () -> memberService.register(new MemberRegisterDto(UID, NAME, EMAIL, NICKNAME, IMG_URL, INTRODUCE, AGE)));
    }
    
    @Test
    @DisplayName("닉네임 변경 테스트")
    public void editNicknameTest() throws Exception {
        //given
        //when
        memberService.editNickname(member, "가나다라");
        
        //then
        assertThat(member.getNickname()).isEqualTo("가나다라");
    }

     @Test
     public void editIntroduceTest() throws Exception {
         //given
         //when
         String introduce = "강아지죠아";
         memberService.editIntroduce(member, introduce);

         //then
         assertThat(member.getIntroduce()).isEqualTo(introduce);
     }

}