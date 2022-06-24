package petPeople.pet.domain.notification.entity;


import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import petPeople.pet.domain.base.BaseTimeEntity;
import petPeople.pet.domain.member.entity.Member;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Notification extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_member_id")
    private Member ownerMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private boolean isChecked;
}
