package petPeople.pet.controller.meeting.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import petPeople.pet.domain.meeting.entity.vo.Category;
import petPeople.pet.domain.meeting.entity.vo.MeetingType;
import petPeople.pet.domain.meeting.entity.vo.Sex;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "모임 개설 요청 DTO")
public class MeetingCreateReqDto {

    @ApiModelProperty(required = true, value = "모임 제목", example = "강아지 좋아하는 사람만")
    private String title;

    @ApiModelProperty(required = true, value = "모임 내용", example = "강아지 산책하러 가요")
    private String content;

    @ApiModelProperty(required = true, value = "도", example = "서울")
    private String doName;

    @ApiModelProperty(required = true, value = "시군구", example = "강북구")
    private String sigungu;

    @ApiModelProperty(required = true, value = "지역", example = "한겅")
    private String location;

    @ApiModelProperty(required = true, value = "성별", example = "남/여")
    private Sex sex;

    @ApiModelProperty(required = true, value = "조건", example = "탈모 금지")
    private String conditions;

    @ApiModelProperty(required = true, value = "카테고리", example = "PICTURE")
    private Category category;

    @ApiModelProperty(required = true, value = "모임 주기", example = "1회성, 주기성")
    private MeetingType meetingType;

    @ApiModelProperty(required = true, value = "1회성일 경우 작성 x/주기 적인 만남일 경우만 작성", example = "주 2회")
    private String period;

    @ApiModelProperty(required = true, value = "촤대 몇명", example = "3")
    private Integer maxPeople;

    @ApiModelProperty(required = true, value = "모임 이미지", example = "www.img.com")
    private List<String> imgUrlList = new ArrayList<>();

}
