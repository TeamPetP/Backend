package petPeople.pet.controller.post.dto.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.data.domain.Slice;

import java.util.List;

@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "댓글 작성 응답 DTO")
public class CommentRetrieveWithCountRespDto {

    @ApiModelProperty(required = true, value = "댓글의 개수", example = "15")
    private Long commentCnt;

    @ApiModelProperty(required = true, value = "댓글 응답 dto", example = "[\n" +
            "                {\n" +
            "                    \"commentId\": 11,\n" +
            "                    \"memberId\": 3,\n" +
            "                    \"postId\": 4,\n" +
            "                    \"content\": \"사랑으로 키울게\",\n" +
            "                    \"likeCnt\": 2,\n" +
            "                    \"createdDate\": \"2022-07-13T08:30:54.86814\",\n" +
            "                    \"nickName\": \"방울이엄마\",\n" +
            "                    \"memberImageUrl\": \"htttestp:www.balladang.com\",\n" +
            "                    \"isLiked\": false,\n" +
            "                    \"childComment\": []\n" +
            "                }\n" +
            "            ]")
    private List<CommentRetrieveRespDto> commentRetrieveRespDtoList;

}
