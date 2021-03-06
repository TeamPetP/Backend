package petPeople.pet.controller.post.dto.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.post.entity.Post;
import petPeople.pet.domain.post.entity.PostImage;
import petPeople.pet.domain.post.entity.Tag;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ApiModel(description = "게시글 조회 응답 DTO")
public class PostRetrieveRespDto {

    @ApiModelProperty(required = true, value = "게시글 ID", example = "1")
    private Long postId;

    @ApiModelProperty(required = true, value = "회원 ID", example = "2")
    private Long memberId;

    @ApiModelProperty(required = true, value = "게시글 내용", example = "강아지 좋아")
    private String content;

    @ApiModelProperty(required = true, value = "게시글 태그", example = "강아지, cute")
    private List<String> tagList = new ArrayList<>();

    @ApiModelProperty(required = true, value = "게시글 이미지 url", example = "www.img.com, www.img.com, www.img.com")
    private List<String> imgUrlList = new ArrayList<>();

    @ApiModelProperty(required = true, value = "작성 시간", example = "2022-06-28T07:38:14.152321")
    private LocalDateTime createdDate;

    @ApiModelProperty(required = true, value = "수정 시간", example = "2022-06-28T07:38:14.152321")
    private LocalDateTime lastModifiedDate;

    @ApiModelProperty(required = true, value = "좋아요 수", example = "3")
    private Long likeCnt;

    @ApiModelProperty(required = true, value = "좋아요 여부", example = "true/false")
    private Boolean isLiked;

    @ApiModelProperty(required = true, value = "회원 이름", example = "김상운")
    private String nickname;

    @ApiModelProperty(required = true, value = "회원 이미지 url", example = "www.img.url")
    private String imgUrl;

    @ApiModelProperty(required = true, value = "댓글 개수", example = "1")
    private Long commentCnt;

    @ApiModelProperty(required = true, value = "자신의 게시글인지", example = "true/false")
    private Boolean owner;

    public PostRetrieveRespDto(Post post, List<Tag> tags, List<PostImage> imgUrls, Long likeCnt, Boolean isLiked) {
        this.postId = post.getId();
        this.memberId = post.getMember().getId();
        this.content = post.getContent();

        for (Tag tag : tags) {
            this.tagList.add(tag.getTag());
        }

        for (PostImage imgUrl : imgUrls) {
            this.imgUrlList.add(imgUrl.getImgUrl());
        }

        this.createdDate = post.getCreatedDate();
        this.lastModifiedDate = post.getLastModifiedDate();
        this.likeCnt = likeCnt;
        this.isLiked = isLiked;
    }

    public PostRetrieveRespDto(Post post, List<Tag> tags, List<PostImage> imgUrls, Long likeCnt, Boolean isLiked, Long commentCnt, Member member) {
        this.postId = post.getId();
        this.memberId = post.getMember().getId();
        this.content = post.getContent();

        for (Tag tag : tags) {
            this.tagList.add(tag.getTag());
        }

        for (PostImage imgUrl : imgUrls) {
            this.imgUrlList.add(imgUrl.getImgUrl());
        }

        this.createdDate = post.getCreatedDate();
        this.lastModifiedDate = post.getLastModifiedDate();
        this.likeCnt = likeCnt;
        this.isLiked = isLiked;

        this.nickname = post.getMember().getNickname();
        this.imgUrl = post.getMember().getImgUrl();
        this.commentCnt = commentCnt;

        if (member == null) {
            this.owner = null;
        } else {
            if (post.getMember() == member) this.owner = true;
            else this.owner = false;
        }

    }
}
