package petPeople.pet.controller.comment.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "댓글 작성 요청 DTO")
public class CommentWriteReqDto {

    @ApiModelProperty(required = true, value = "댓글 수정 내용", example = "같이 거북이 산책 시킬래요?")
    private String content;

    @ApiModelProperty(required = false, value = "부모 댓글 ID", example = "1")
    private Long parentId;
}
