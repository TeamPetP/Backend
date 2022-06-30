package petPeople.pet.domain.post.entity;

import lombok.*;
import petPeople.pet.domain.base.BaseTimeEntity;
import petPeople.pet.domain.member.entity.Member;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter @Setter
@Builder
public class PostBookmark extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "post_bookmark_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

}
