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
public class MeetingEditRespDto {

    private Long meetingId;

    private String title;

    private String content;

    private String doName;

    private String sigungu;

    private String location;

    private String sex;

    private String conditions;

    private String category;

    private String meetingType;

    private String period;

    private LocalDateTime meetingDate;

    private Integer maxPeople;

    private List<String> imgUrlList = new ArrayList<>();

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    private Boolean status;

    public MeetingEditRespDto(Meeting meeting, List<MeetingImage> meetingImageList) {
        this.meetingId = meeting.getId();
        this.title = meeting.getTitle();
        this.content = meeting.getContent();
        this.doName = meeting.getDoName();
        this.sigungu = meeting.getSigungu();
        this.location = meeting.getLocation();
        this.sex = meeting.getSex().getDetail();
        this.conditions = meeting.getConditions();
        this.category = meeting.getCategory().getDetail();
        this.meetingType = meeting.getMeetingType().getDetail();
        this.period = meeting.getPeriod();
        this.meetingDate = meeting.getMeetingDate();
        this.maxPeople = meeting.getMaxPeople();

        for (MeetingImage meetingImage : meetingImageList) {
            imgUrlList.add(meetingImage.getImgUrl());
        }

        this.createdDate = meeting.getCreatedDate();
        this.lastModifiedDate = meeting.getLastModifiedDate();
        this.status = meeting.getIsOpened();
    }
}
