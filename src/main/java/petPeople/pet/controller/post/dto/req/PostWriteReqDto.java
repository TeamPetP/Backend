package petPeople.pet.controller.post.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "게시글 작성 요청 DTO")
public class PostWriteReqDto {

    @ApiModelProperty(required = true, value = "게시글 내용", example = "강아지 좋아")
    private String content;

    @ApiModelProperty(required = true, value = "게시글 태그", example = "강아지, cute")
    private List<String> tagList = new ArrayList<>();

    @ApiModelProperty(required = true, value = "게시글 이미지 url", example = "www.img.com, www.img.com, www.img.com")
    private List<String> imgUrlList = new ArrayList<>();
}
