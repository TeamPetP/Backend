package petPeople.pet.domain.member.service;

import com.google.firebase.auth.FirebaseToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import petPeople.pet.controller.member.dto.req.MemberLocalRegisterReqDto;
import petPeople.pet.controller.member.dto.req.MemberRegisterReqDto;

@AllArgsConstructor
@Getter @Setter
public class MemberRegisterDto {


    private String uid;

    private String name;

    private String email;

    private String nickname;

    private String imgUrl;

    private String introduce;

    public MemberRegisterDto(MemberLocalRegisterReqDto memberLocalRegisterReqDto) {
        this.uid = memberLocalRegisterReqDto.getUid();
        this.name = memberLocalRegisterReqDto.getName();
        this.email = memberLocalRegisterReqDto.getEmail();
        this.nickname = memberLocalRegisterReqDto.getNickname();
        this.imgUrl = memberLocalRegisterReqDto.getImgUrl();
        this.introduce = memberLocalRegisterReqDto.getIntroduce();
    }

    public MemberRegisterDto(FirebaseToken firebaseToken, MemberRegisterReqDto memberRegisterReqDto) {
        this.uid = firebaseToken.getUid();
        this.name = firebaseToken.getName();
        this.email = firebaseToken.getEmail();
        this.nickname = memberRegisterReqDto.getNickname();
        this.imgUrl = firebaseToken.getPicture();
        this.introduce = memberRegisterReqDto.getIntroduce();
    }
}
