package petPeople.pet.controller.meeting.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import petPeople.pet.domain.meeting.entity.Category;
import petPeople.pet.domain.meeting.entity.MeetingType;
import petPeople.pet.domain.meeting.entity.Sex;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingEditReqDto {

    private String title;

    private String content;

    private String doName;

    private String sigungu;

    private String location;

    private Sex sex;

    private String conditions;

    private Category category;

    private MeetingType meetingType;

    private String period;

    private LocalDateTime meetingDate;

    private Integer maxPeople;

    private Boolean isOpened;

    private List<String> imgUrlList = new ArrayList<>();

}
