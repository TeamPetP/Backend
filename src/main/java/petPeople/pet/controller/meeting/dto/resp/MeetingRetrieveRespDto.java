package petPeople.pet.controller.meeting.dto.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "모임 게시글 조회 응답 DTO")
public class MeetingRetrieveRespDto {

    @ApiModelProperty(required = true, value = "모임 ID", example = "1")
    private Long meetingId;

    @ApiModelProperty(required = true, value = "회원 ID", example = "1")
    private Long memberId;

    @ApiModelProperty(required = true, value = "회원 닉네임", example = "abcd")
    private String nickname;

    @ApiModelProperty(required = true, value = "모임 모집 여부", example = "true/false")
    private Boolean isOpened;

    @ApiModelProperty(required = true, value = "도", example = "서울")
    private String doName;

    @ApiModelProperty(required = true, value = "시군구", example = "강북구")
    private String sigungu;

    @ApiModelProperty(required = true, value = "지역", example = "한겅")
    private String location;

    @ApiModelProperty(required = true, value = "카테고리", example = "PICTURE")
    private String category;

    @ApiModelProperty(required = true, value = "모임 주기", example = "1회성, 주기성")
    private String meetingType;

    @ApiModelProperty(required = true, value = "1회성일 경우 작성 x/주기 적인 만남일 경우만 작성", example = "주 2회")
    private String period;

    @ApiModelProperty(required = true, value = "모임 제목", example = "강아지 좋아하는 사람만")
    private String title;

    @ApiModelProperty(required = true, value = "모임 내용", example = "강아지 산책하러 가요")
    private String content;

    @ApiModelProperty(required = true, value = "조건", example = "탈모 금지")
    private String conditions;

    @ApiModelProperty(required = true, value = "성별", example = "남/여")
    private String sex;

    @ApiModelProperty(required = true, value = "모임 최대 인원", example = "3")
    private Integer maxPeople;

    @ApiModelProperty(required = true, value = "가입한 회원 수", example = "3")
    private Integer joinPeople;

    @ApiModelProperty(required = true, value = "작성 시간", example = "2022-06-28T07:38:14.152321")
    private LocalDateTime createDate;

    @ApiModelProperty(required = true, value = "이미지 url 리스트", example = "www.img.com")
    private List<String> imgUrlList = new ArrayList<>();

    @ApiModelProperty(required = true, value = "가입 회원 리스트", example = "aaaa, bbbb, cccc")
    private List<String> joinMembers = new ArrayList<>();

    @ApiModelProperty(required = true, value = "모집 여부", example = "true/false")
    private Boolean isJoined;

    public MeetingRetrieveRespDto(Meeting meeting, List<MeetingImage> meetingImageList, List<MeetingMember> meetingMemberList, Boolean isJoined) {
        this.meetingId = meeting.getId();
        this.memberId = meeting.getMember().getId();
        this.nickname = meeting.getMember().getNickname();
        this.isOpened = meeting.getIsOpened();
        this.doName = meeting.getDoName();
        this.sigungu = meeting.getSigungu();
        this.location = meeting.getLocation();
        this.category = meeting.getCategory().getDetail();
        this.meetingType = meeting.getMeetingType().getDetail();
        this.period = meeting.getPeriod();
        this.title = meeting.getTitle();
        this.content = meeting.getContent();
        this.conditions = meeting.getConditions();
        this.sex = meeting.getSex().getDetail();
        this.maxPeople = meeting.getMaxPeople();
        this.joinPeople = meetingMemberList.size();
        this.createDate = meeting.getCreatedDate();
        for (MeetingMember meetingMember : meetingMemberList) {
            joinMembers.add(meetingMember.getMember().getNickname());
        }

        for (MeetingImage meetingImage : meetingImageList) {
            imgUrlList.add(meetingImage.getImgUrl());
        }
        this.isJoined = isJoined;
    }
}
