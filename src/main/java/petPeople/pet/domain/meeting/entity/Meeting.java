package petPeople.pet.domain.meeting.entity;

import lombok.*;
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

    private LocalDateTime endDate;

    private LocalDateTime meetingDate;

    private String conditions;

    private Integer maxPeople;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Enumerated(EnumType.STRING)
    private Category category;

    private Integer maxAge;

    private Integer minAge;

    private String title;

    private String content;

    private Boolean isOpened;

}
