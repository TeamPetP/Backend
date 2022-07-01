package petPeople.pet.controller.comment.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ApiModel(description = "게시글 수정 요청 DTO")
public class CommentEditReqDto {

    @ApiModelProperty(required = true, value = "댓글 내용", example = "같이 물고기 산책 시킬래요?")
    private String content;
}
