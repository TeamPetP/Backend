package petPeople.pet.controller.meeting.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
@ApiModel(description = "모임 게시글 작성 요청 DTO")
public class MeetingPostWriteReqDto {

    @ApiModelProperty(required = true, value = "모임 게시글 제목", example = "강아지 좋아하는 사람만")
    private String title;

    @ApiModelProperty(required = true, value = "모임 게시글 내용", example = "강아지 산책하러 가요")
    private String content;

    @ApiModelProperty(required = true, value = "모임 이미지", example = "www.img.com")
    private List<String> imgUrlList = new ArrayList<>();
}
