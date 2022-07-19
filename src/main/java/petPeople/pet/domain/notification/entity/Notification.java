package petPeople.pet.domain.notification.entity;


import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;
import petPeople.pet.domain.base.BaseTimeEntity;
import petPeople.pet.domain.comment.entity.Comment;
import petPeople.pet.domain.meeting.entity.JoinRequestStatus;
import petPeople.pet.domain.meeting.entity.Meeting;
import petPeople.pet.domain.meeting.entity.MeetingComment;
import petPeople.pet.domain.meeting.entity.MeetingPost;
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

    @Id
    @GeneratedValue
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_post_id")
    @Nullable
    private MeetingPost meetingPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_post_id2")
    private MeetingPost meetingWritePost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_comment_id")
    private MeetingComment meetingComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_comment_id2")
    private MeetingComment writeMeetingComment;

    @Enumerated(EnumType.STRING)
    private JoinRequestStatus meetingJoinRequestFlag;

    private boolean isChecked;

    public void changeIsChecked() {
        this.isChecked = true;
    }
}
