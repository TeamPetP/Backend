package petPeople.pet.domain.meeting;

import lombok.*;
import petPeople.pet.domain.base.BaseTimeEntity;
import petPeople.pet.domain.member.Member;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter @Setter
@Builder
public class Meeting extends BaseTimeEntity {

    @Id @Generated
    @Column(name = "meeting_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String doName;

    private LocalDateTime meetingDay;

    private Integer maxPeople;

    @Enumerated(EnumType.STRING)
    private Participant participant;

    @Enumerated(EnumType.STRING)
    private Category category;

    private Integer age;

    private String title;

    private String content;

    private Boolean isOpened;

}
