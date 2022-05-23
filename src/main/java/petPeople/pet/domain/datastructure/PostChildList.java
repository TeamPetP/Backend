package petPeople.pet.domain.datastructure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import petPeople.pet.domain.post.entity.PostImage;
import petPeople.pet.domain.post.entity.PostLike;
import petPeople.pet.domain.post.entity.Tag;

import java.util.List;

@Data
@Builder
public class PostChildList {

    List<Tag> tagList;
    List<PostImage> postImageList;
    List<PostLike> postLikeList;



}
