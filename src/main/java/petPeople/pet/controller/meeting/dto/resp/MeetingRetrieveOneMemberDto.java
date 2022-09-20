package petPeople.pet.controller.meeting.dto.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import petPeople.pet.domain.meeting.entity.Meeting;
import petPeople.pet.domain.meeting.entity.MeetingImage;
import petPeople.pet.domain.meeting.entity.MeetingMember;
import petPeople.pet.domain.member.entity.Member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingRetrieveOneMemberDto {

    private Long memberId;

    private String nickname;

    private String memberImgUrl;

    public MeetingRetrieveOneMemberDto(MeetingMember meetingMember) {
        this.memberId = meetingMember.getMember().getId();
        this.nickname = meetingMember.getMember().getNickname();
        this.memberImgUrl = meetingMember.getMember().getImgUrl();
    }
}
