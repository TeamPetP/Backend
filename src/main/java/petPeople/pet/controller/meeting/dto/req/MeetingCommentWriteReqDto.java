package petPeople.pet.controller.meeting.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@EqualsAndHashCode
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "모임 게시글 댓글 작성 요청 DTO")
public class MeetingCommentWriteReqDto {

    @ApiModelProperty(required = true, value = "댓글 내용", example = "강아지 좋아")
    private String content;

}
