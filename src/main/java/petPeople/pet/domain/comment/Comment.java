package petPeople.pet.domain.comment;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import petPeople.pet.domain.base.BaseTimeEntity;
import petPeople.pet.domain.member.Member;
import petPeople.pet.domain.post.Post;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@EntityListeners(AuditingEntityListener.class)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class Comment extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @JoinColumn(name = "parent_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent")
    List<Comment> child = new ArrayList<>();

    @Lob
    private String content;

}
