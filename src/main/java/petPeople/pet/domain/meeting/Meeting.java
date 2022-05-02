package petPeople.pet.domain.meeting;

import lombok.*;
import petPeople.pet.domain.member.Member;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter @Setter
@Builder
public class Meeting {

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
    private Category category;

    private String title;

    private String content;

    private boolean isOpened;

    // TODO: 2022-05-03 이미지 추가
}
