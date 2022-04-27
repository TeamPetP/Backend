package petPeople.pet.domain.comment;

import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import petPeople.pet.domain.base.BaseTimeEntity;
import petPeople.pet.domain.commentlike.CommentLike;
import petPeople.pet.domain.member.Member;
import petPeople.pet.domain.post.Post;

import javax.persistence.*;
import java.time.LocalDateTime;
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

    @Builder.Default
    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<CommentLike> commentLikeList = new ArrayList<>();

    @JoinColumn(name = "parent_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent")
    List<Comment> child = new ArrayList<>();

    @Lob
    private String content;

    private int likeCnt;

    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

}
