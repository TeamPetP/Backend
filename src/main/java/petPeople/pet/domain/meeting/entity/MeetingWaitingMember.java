package petPeople.pet.domain.meeting.entity;

import lombok.*;
import petPeople.pet.domain.base.BaseTimeEntity;
import petPeople.pet.domain.meeting.entity.vo.JoinRequestStatus;
import petPeople.pet.domain.member.entity.Member;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class MeetingWaitingMember extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "meeting_waiting_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    @Enumerated(EnumType.STRING)
    private JoinRequestStatus joinRequestStatus;

}
