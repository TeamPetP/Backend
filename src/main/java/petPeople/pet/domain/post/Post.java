package petPeople.pet.domain.post;

import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import petPeople.pet.domain.base.BaseTimeEntity;
import petPeople.pet.domain.comment.Comment;
import petPeople.pet.domain.member.Member;
import petPeople.pet.domain.notification.Notification;
import petPeople.pet.domain.postimage.PostImage;
import petPeople.pet.domain.postlike.PostLike;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> postImageList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PostLike> postLikeList = new ArrayList<>();

    private String title;

    @Lob
    private String content;

    private String hashTag;

    private int likeCnt;

    private int commentCnt;

    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

}
