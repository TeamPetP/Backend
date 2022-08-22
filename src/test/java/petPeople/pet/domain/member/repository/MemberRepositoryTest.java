package petPeople.pet.domain.member.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import petPeople.pet.common.BaseControllerTest;
import petPeople.pet.domain.member.entity.Member;

class MemberRepositoryTest extends BaseControllerTest {

    @Autowired
    MemberRepository memberRepository;

    @DisplayName("uid 로 member 를 찾는 테스트")
    @Test
    public void findByUidTest() throws Exception {

        //given
        String uid = "abcd";
        String email = "issiscv@naver.com";
        String name = "김상운";
        String nickname = "balladang";
        String imgUrl = "www.imgurl.com";
        String introduce = "잘지내요 우리";

        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Member saveMember = memberRepository.save(member);

        //when
        Member findMember = memberRepository.findByUid(uid).get();

        //then
        Assertions.assertThat(findMember).isEqualTo(saveMember);
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