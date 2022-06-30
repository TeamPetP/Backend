package petPeople.pet.controller.member.dto.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ApiModel(description = "회원의 북마크 조회 응답 DTO")
public class MemberPostBookMarkRespDto {

    @ApiModelProperty(required = true, value = "북마크 ID", example = "1")
    private Long postBookMarkId;

    @ApiModelProperty(required = true, value = "게시글 ID", example = "2")
    private Long postId;

    @ApiModelProperty(required = true, value = "게시글 내용", example = "강아지 귀여워")
    private String content;
}
