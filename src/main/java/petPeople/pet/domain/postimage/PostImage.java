package petPeople.pet.domain.postimage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import petPeople.pet.domain.base.BaseTimeEntity;
import petPeople.pet.domain.member.Member;
import petPeople.pet.domain.post.Post;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PostImage extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "post_image_id")
    private Long id;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    private String imgUrl;
}
