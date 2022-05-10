package petPeople.pet.controller.post.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
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
public class PostWriteRespDto {

    private Long postId;

    private String content;

    private List<String> tagList = new ArrayList<>();

    private List<String> imgUrlList = new ArrayList<>();

    private LocalDateTime createdDate;

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
