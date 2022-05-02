package petPeople.pet.domain.post;

import lombok.*;
import petPeople.pet.domain.base.BaseTimeEntity;
import petPeople.pet.domain.member.Member;
import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter @Setter
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Lob
    private String content;

    private String hashTag;

}
