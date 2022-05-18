package petPeople.pet.controller.meeting.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import petPeople.pet.domain.meeting.entity.Category;
import petPeople.pet.domain.meeting.entity.Meeting;
import petPeople.pet.domain.meeting.entity.MeetingImage;
import petPeople.pet.domain.meeting.entity.Sex;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingCreateRespDto {

    private Long meetingId;

    private String title;

    private String content;

    private String doName;

    private String sigungu;

    private Sex sex;

    private String conditions;

    private Category category;

    private Integer minAge;

    private Integer maxAge;

    private LocalDateTime endDate;

    private LocalDateTime meetingDate;

    private Integer maxPeople;

    private List<String> imgUrlList = new ArrayList<>();

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    public MeetingCreateRespDto(Meeting meeting, List<MeetingImage> meetingImageList) {
        this.meetingId = meeting.getId();
        this.title = meeting.getTitle();
        this.content = meeting.getContent();
        this.doName = meeting.getDoName();
        this.sigungu = meeting.getSigungu();
        this.sex = meeting.getSex();
        this.conditions = meeting.getConditions();
        this.category = meeting.getCategory();
        this.minAge = meeting.getMinAge();
        this.maxAge = meeting.getMaxAge();
        this.endDate = meeting.getEndDate();
        this.meetingDate = meeting.getMeetingDate();
        this.maxPeople = meeting.getMaxPeople();

        for (MeetingImage meetingImage : meetingImageList) {
            imgUrlList.add(meetingImage.getImgUrl());
        }

        this.createdDate = meeting.getCreatedDate();
        this.lastModifiedDate = meeting.getLastModifiedDate();
    }
}
