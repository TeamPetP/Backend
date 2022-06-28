package petPeople.pet.controller.member.dto.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import petPeople.pet.domain.member.entity.Member;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
@EqualsAndHashCode
@ToString
@ApiModel(description = "회원가입 응답 dto")
public class MemberRegisterRespDto {

    @ApiModelProperty(required = true, value = "회원 ID", example = "1")
    private Long memberId;

    @ApiModelProperty(required = true, value = "회원 uid", example = "회원 uid")
    private String uid;

    @ApiModelProperty(required = true, value = "회원 이메일", example = "abcd@naver.com")
    private String email;

    @ApiModelProperty(required = true, value = "회원 이름", example = "김상운")
    private String name;

    @ApiModelProperty(required = true, value = "회원 닉네임", example = "abcd")
    private String nickname;

    @ApiModelProperty(required = true, value = "회원 이미지", example = "www.img.com")
    private String imgUrl;

    @ApiModelProperty(required = true, value = "회원 한줄 소개", example = "저는 개입니다.")
    private String introduce;

    public MemberRegisterRespDto(Member member) {
        this.memberId = member.getId();
        this.uid = member.getUid();
        this.email = member.getEmail();
        this.name = member.getName();
        this.nickname = member.getNickname();
        this.imgUrl = member.getImgUrl();
        this.introduce = member.getIntroduce();
    }

}
