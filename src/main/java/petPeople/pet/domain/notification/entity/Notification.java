package petPeople.pet.domain.notification.entity;


import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import petPeople.pet.domain.base.BaseTimeEntity;
import petPeople.pet.domain.comment.entity.Comment;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.post.entity.Post;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id2")
    private Comment writeComment;

    private boolean isChecked;

    public void changeIsChecked() {
        this.isChecked = true;
    }
}
