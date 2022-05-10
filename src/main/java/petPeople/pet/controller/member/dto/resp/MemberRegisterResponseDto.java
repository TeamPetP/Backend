package petPeople.pet.controller.member.dto.resp;

import lombok.*;
import petPeople.pet.domain.member.entity.Member;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
@EqualsAndHashCode
@ToString
public class MemberRegisterResponseDto {

    private Long memberId;

    private String uid;

    private String email;

    private String name;

    private String nickname;

    private String imgUrl;

    private String introduce;

    public MemberRegisterResponseDto(Member member) {
        this.memberId = member.getId();
        this.uid = member.getUid();
        this.email = member.getEmail();
        this.name = member.getName();
        this.nickname = member.getNickname();
        this.imgUrl = member.getImgUrl();
        this.introduce = member.getIntroduce();
    }

}
