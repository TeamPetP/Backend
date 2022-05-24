package petPeople.pet.domain.meeting.entity;

import lombok.*;
import petPeople.pet.controller.meeting.dto.req.MeetingEditReqDto;
import petPeople.pet.domain.base.BaseTimeEntity;
import petPeople.pet.domain.member.entity.Member;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter @Setter
@Builder
public class Meeting extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "meeting_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String doName;

    private String sigungu;

    private String location;

    private LocalDateTime meetingDate;

    private String conditions;

    private Integer maxPeople;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private MeetingType meetingType;

    private String period;

    private String title;

    private String content;

    private Boolean isOpened;

    public void edit(MeetingEditReqDto meetingEditReqDto) {
        this.doName = meetingEditReqDto.getDoName();
        this.sigungu = meetingEditReqDto.getSigungu();
        this.conditions = meetingEditReqDto.getConditions();
        this.maxPeople = meetingEditReqDto.getMaxPeople();
        this.sex = meetingEditReqDto.getSex();
        this.category = meetingEditReqDto.getCategory();
        this.title = meetingEditReqDto.getTitle();
        this.content = meetingEditReqDto.getContent();
        this.isOpened = meetingEditReqDto.getIsOpened();
    }

}
