package petPeople.pet.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String uid) throws UsernameNotFoundException {
        return memberRepository.findByUid(uid)
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException("해당 회원이 존재하지 않습니다.");
                });
    }

    @Transactional
    public void save(final MemberRegisterDto memberRegisterDto) {
        Member member = createMember(memberRegisterDto);
        memberRepository.save(member);
    }

    private Member createMember(MemberRegisterDto memberRegisterDto) {
        return Member.builder()
                .uid(memberRegisterDto.getUid())
                .email(memberRegisterDto.getEmail())
                .name(memberRegisterDto.getName())
                .nickname(memberRegisterDto.getNickname())
                .imgUrl(memberRegisterDto.getImgUrl())
                .introduce(memberRegisterDto.getIntroduce())
                .build();
    }
}
