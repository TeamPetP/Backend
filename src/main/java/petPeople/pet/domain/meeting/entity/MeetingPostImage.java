package petPeople.pet.domain.meeting.entity;

import lombok.*;
import petPeople.pet.domain.base.BaseTimeEntity;
import petPeople.pet.domain.member.entity.Member;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter @Setter
@Builder
public class MeetingPostImage extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "meeting_post_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_post_id")
    private MeetingPost meetingPost;

    private String imgUrl;
}
