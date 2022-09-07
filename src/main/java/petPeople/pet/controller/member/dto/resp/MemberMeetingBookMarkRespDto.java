package petPeople.pet.controller.member.dto.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import petPeople.pet.domain.meeting.entity.MeetingBookmark;
import petPeople.pet.domain.meeting.entity.MeetingImage;
import petPeople.pet.domain.meeting.entity.MeetingMember;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ApiModel(description = "회원의 북마크 조회 응답 DTO")
public class MemberMeetingBookMarkRespDto {

    @ApiModelProperty(required = true, value = "모임 북마크 ID", example = "1")
    private Long meetingBookMarkId;

    @ApiModelProperty(required = true, value = "모임 ID", example = "1")
    private Long meetingId;

    @ApiModelProperty(required = true, value = "회원 ID", example = "1")
    private Long memberId;

    @ApiModelProperty(required = true, value = "회원 닉네임", example = "abcd")
    private String nickname;

    @ApiModelProperty(required = true, value = "회원 사진", example = "www.img.com")
    private String memberImgUrl;

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

    @ApiModelProperty(required = true, value = "성별", example = "남/여")
    private String sex;

    @ApiModelProperty(required = true, value = "1회성일 경우 작성 x/주기 적인 만남일 경우만 작성", example = "주 2회")
    private String period;

    @ApiModelProperty(required = true, value = "모임 제목", example = "강아지 좋아하는 사람만")
    private String title;

    @ApiModelProperty(required = true, value = "모임 내용", example = "강아지 산책하러 가요")
    private String content;

    @ApiModelProperty(required = true, value = "조건", example = "탈모 금지")
    private String conditions;

    @ApiModelProperty(required = true, value = "모임 최대 인원", example = "3")
    private Integer maxPeople;

    @ApiModelProperty(required = true, value = "가입한 회원 수", example = "3")
    private Integer joinPeople;

    @ApiModelProperty(required = true, value = "작성 시간", example = "2022-06-28T07:38:14.152321")
    private LocalDateTime createDate;

    @ApiModelProperty(required = true, value = "이미지 url 리스트", example = "www.img.com")
    private List<String> imgUrlList = new ArrayList<>();

    @ApiModelProperty(required = true, value = "모집 여부", example = "true/false")
    private Boolean isJoined;


    public MemberMeetingBookMarkRespDto(MeetingBookmark meetingBookmark, List<MeetingMember> meetingMemberList, List<MeetingImage> meetingImageList, boolean isJoined) {
        this.meetingBookMarkId = meetingBookmark.getId();
        this.meetingId = meetingBookmark.getMeeting().getId();
        this.memberId = meetingBookmark.getMeeting().getMember().getId();
        this.nickname = meetingBookmark.getMeeting().getMember().getNickname();
        this.memberImgUrl = meetingBookmark.getMeeting().getMember().getImgUrl();
        this.isOpened = meetingBookmark.getMeeting().getIsOpened();
        this.doName = meetingBookmark.getMeeting().getDoName();
        this.sigungu = meetingBookmark.getMeeting().getSigungu();
        this.location = meetingBookmark.getMeeting().getLocation();
        this.sex = meetingBookmark.getMeeting().getSex().getDetail();
        this.category = meetingBookmark.getMeeting().getCategory().getDetail();
        this.meetingType = meetingBookmark.getMeeting().getMeetingType().getDetail();
        this.period = meetingBookmark.getMeeting().getPeriod();
        this.title = meetingBookmark.getMeeting().getTitle();
        this.content = meetingBookmark.getMeeting().getContent();
        this.conditions = meetingBookmark.getMeeting().getConditions();
        this.maxPeople = meetingBookmark.getMeeting().getMaxPeople();
        this.joinPeople = meetingMemberList.size();
        this.createDate = meetingBookmark.getMeeting().getCreatedDate();
        this.isJoined = isJoined;

        for (MeetingImage meetingImage : meetingImageList) {
            imgUrlList.add(meetingImage.getImgUrl());
        }

    }
}
