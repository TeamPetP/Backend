package petPeople.pet.domain.member.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import petPeople.pet.controller.member.dto.req.MemberLocalRegisterRequestDto;

@AllArgsConstructor
@Getter @Setter
public class MemberRegisterDto {


    private String uid;

    private String name;

    private String email;

    private String nickname;

    private String imgUrl;

    private String introduce;

    public MemberRegisterDto(MemberLocalRegisterRequestDto memberLocalRegisterRequestDto) {
        this.uid = memberLocalRegisterRequestDto.getUid();
        this.name = memberLocalRegisterRequestDto.getName();
        this.email = memberLocalRegisterRequestDto.getEmail();
        this.nickname = memberLocalRegisterRequestDto.getNickname();
        this.imgUrl = memberLocalRegisterRequestDto.getImgUrl();
        this.introduce = memberLocalRegisterRequestDto.getIntroduce();
    }
}
