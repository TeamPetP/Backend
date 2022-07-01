package petPeople.pet.controller.member.dto.resp.notificationResp;

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
public class MemberMeetingBookMarkRespDto {

    @ApiModelProperty(required = true, value = "모임 북마크 ID", example = "1")
    private Long meetingBookMarkId;

    @ApiModelProperty(required = true, value = "모임 ID", example = "2")
    private Long meetingId;

    @ApiModelProperty(required = true, value = "모임 제목", example = "물고기 산책시킬 분")
    private String title;
}
