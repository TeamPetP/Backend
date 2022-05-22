package petPeople.pet.controller.meeting.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import petPeople.pet.domain.meeting.entity.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingRetrieveRespDto {

    private Long meetingId;
    private Long memberId;
    private String nickname;
    private Boolean status;
    private String doName;
    private String sigungu;
    private String category;
    private String title;
    private String content;
    private LocalDateTime endDate;
    private String conditions;
    private Integer maxPeople;
    private Integer joinPeople;
    private LocalDateTime createDate;
    private List<String> imgUrlList = new ArrayList<>();
    private List<String> joinMembers = new ArrayList<>();


    public MeetingRetrieveRespDto(Meeting meeting, List<MeetingImage> meetingImageList, List<MeetingMember> meetingMemberList) {
        this.meetingId = meeting.getId();
        this.memberId = meeting.getMember().getId();
        this.nickname = meeting.getMember().getNickname();
        this.status = meeting.getIsOpened();
        this.doName = meeting.getDoName();
        this.sigungu = meeting.getSigungu();
        this.category = meeting.getCategory().getDetail();
        this.title = meeting.getTitle();
        this.content = meeting.getContent();
        this.endDate = meeting.getEndDate();
        this.conditions = meeting.getConditions();
        this.maxPeople = meeting.getMaxPeople();
        this.joinPeople = meetingMemberList.size();
        this.createDate = meeting.getCreatedDate();
        for (MeetingMember meetingMember : meetingMemberList) {
            joinMembers.add(meetingMember.getMember().getNickname());
        }

        for (MeetingImage meetingImage : meetingImageList) {
            imgUrlList.add(meetingImage.getImgUrl());
        }
    }
}