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
@ApiModel(description = "회원 게시글, 알림, 모임 북마크 개수 응답 DTO")
public class MemberCountDto {

    @ApiModelProperty(required = true, value = "회원이 작성한 게시글 개수", example = "1")
    private Long postCnt;

    @ApiModelProperty(required = true, value = "회원이 등록한 모임 북마크 개수", example = "1")
    private Long meetingBookCnt;

    @ApiModelProperty(required = true, value = "알림 개수", example = "1")
    private Long notificationCnt;

}
