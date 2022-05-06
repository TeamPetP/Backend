package petPeople.pet.domain.meeting.entity;

import lombok.*;
import petPeople.pet.domain.member.entity.Member;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingCommentLike {

    @Id @Generated
    @Column(name = "meeting_comment_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_comment_id")
    private MeetingComment meetingComment;
}
