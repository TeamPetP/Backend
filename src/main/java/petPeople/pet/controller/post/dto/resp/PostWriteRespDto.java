package petPeople.pet.controller.post.dto.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import petPeople.pet.domain.post.entity.Post;
import petPeople.pet.domain.post.entity.PostImage;
import petPeople.pet.domain.post.entity.Tag;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "게시글 작성 응답 DTO")
public class PostWriteRespDto {

    @ApiModelProperty(required = true, value = "게시글 ID", example = "1")
    private Long postId;

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

    public PostWriteRespDto(Post post, List<Tag> tags, List<PostImage> imgUrls) {
        this.postId = post.getId();
        this.content = post.getContent();

        for (Tag tag : tags) {
            this.tagList.add(tag.getTag());
        }

        for (PostImage imgUrl : imgUrls) {
            this.imgUrlList.add(imgUrl.getImgUrl());
        }

        this.createdDate = post.getCreatedDate();
        this.lastModifiedDate = post.getLastModifiedDate();
    }
}
