package petPeople.pet.controller.member.dto.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
@ApiModel(description = "모임 신청자 조회 응답 DTO")
@EqualsAndHashCode
public class MeetingWaitingMemberRespDto {

    @ApiModelProperty(required = true, value = "회원 ID", example = "1")
    private Long memberId;

    @ApiModelProperty(required = true, value = "회원 img url", example = "www.img.com")
    private String memberImgUrl;

    @ApiModelProperty(required = true, value = "모임 ID", example = "2")
    private Long meetingId;

    @ApiModelProperty(required = true, value = "회원 닉네임", example = "abcd")
    private String nickname;

    @ApiModelProperty(required = true, value = "회원 한줄 소개", example = "강아지 좋아해")
    private String introduce;

    @ApiModelProperty(required = true, value = "신청 상태", example = "대기중, 승인됨, 거절됨")
    private String joinRequestStatus;

    @ApiModelProperty(required = true, value = "게시글 ID", example = "1")
    private LocalDateTime createDate;
}
