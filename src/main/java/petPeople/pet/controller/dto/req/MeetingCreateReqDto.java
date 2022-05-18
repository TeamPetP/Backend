package petPeople.pet.controller.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import petPeople.pet.domain.meeting.entity.Category;
import petPeople.pet.domain.meeting.entity.Sex;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingCreateReqDto {

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

}
