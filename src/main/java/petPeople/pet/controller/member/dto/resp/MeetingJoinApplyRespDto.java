package petPeople.pet.controller.member.dto.resp;

import io.swagger.annotations.ApiModel;
import lombok.*;
import petPeople.pet.domain.meeting.entity.Category;
import petPeople.pet.domain.meeting.entity.MeetingType;
import petPeople.pet.domain.meeting.entity.Sex;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
@ApiModel(description = "내가 가입한 모임 신청 현황 조회 응답 DTO")
public class MeetingJoinApplyRespDto {

    private Long meetingId;

    private String doName;

    private String sigungu;

    private String location;

    private LocalDateTime meetingDate;

    private String conditions;

    private Integer maxPeople;

    private String sex;

    private String category;

    private String meetingType;

    private String period;

    private String title;

    private String content;

    private Boolean isOpened;

    private String joinRequestStatus;
}
