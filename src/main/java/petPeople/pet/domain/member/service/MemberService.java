package petPeople.pet.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petPeople.pet.controller.member.dto.resp.MemberRegisterResponseDto;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.repository.MemberRepository;
import petPeople.pet.exception.CustomException;
import petPeople.pet.exception.ErrorCode;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(final String uid) throws UsernameNotFoundException {
        return findOptionalMember(uid).orElseThrow(() -> {
                    throw new UsernameNotFoundException("해당 회원이 존재하지 않습니다.");
                });
    }

    @Transactional
    public MemberRegisterResponseDto register(MemberRegisterDto memberRegisterDto) {
        validateDuplicatedMember(memberRegisterDto.getUid());
        return new MemberRegisterResponseDto(saveMember(memberRegisterDto));
    }

    private Member saveMember(MemberRegisterDto memberRegisterDto) {
        return memberRepository.save(createMember(memberRegisterDto));
    }

    private void validateDuplicatedMember(String uid) {
        if (findOptionalMember(uid).isPresent()) {
            throw new CustomException(ErrorCode.EXIST_MEMBER, "해당 계정으로 이미 회원가입을 했습니다.");
        }
    }

    private Optional<Member> findOptionalMember(String uid) {
        return memberRepository.findByUid(uid);
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
