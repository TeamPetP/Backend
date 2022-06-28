package petPeople.pet.controller.member.dto.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class MemberIntroduceRespDto {

    @ApiModelProperty(required = true, value = "회원 한줄 소개", example = "저는 개입니다.")
    private String introduce;

}
