package petPeople.pet.controller.meeting.dto.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import petPeople.pet.controller.post.dto.resp.CommentRetrieveRespDto;

import java.util.List;

@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "댓글 작성 응답 DTO")
public class MeetingCommentRetrieveWithCountRespDto {

    @ApiModelProperty(required = true, value = "댓글의 개수", example = "15")
    private Long commentCnt;

    @ApiModelProperty(required = true, value = "댓글 응답 dto", example = "")
    private List<MeetingCommentRetrieveRespDto> meetingCommentRetrieveRespDtoList;
}
