package petPeople.pet.controller.meeting.dto.resp;

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
@ApiModel(description = "모임의 게시글 이미지 조회 응답 DTO")
public class MeetingPostImageRetrieveRespDto {

    @ApiModelProperty(required = true, value = "모임 이미지 ID", example = "12")
    private Long meetingImageId;

    @ApiModelProperty(required = true, value = "모임 ID", example = "1")
    private Long meetingId;

    @ApiModelProperty(required = true, value = "이미지 url", example = "www.img.com")
    private String imgUrl;

}
