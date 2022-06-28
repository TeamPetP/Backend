package petPeople.pet.controller.member.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "회원 정보 수정 요청 DTO")
public class MemberEditReqDto {

    @ApiModelProperty(required = true, value = "회원 닉네임", example = "abcd")
    private String nickname;

    @ApiModelProperty(required = true, value = "회원 한줄 소개", example = "저는 개입니다.")
    private String introduce;

}
