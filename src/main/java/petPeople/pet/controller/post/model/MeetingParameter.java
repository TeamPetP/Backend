package petPeople.pet.controller.post.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;

@NoArgsConstructor
@Getter @Setter
public class MeetingParameter {

    @ApiModelProperty(required = true, value = "도시", example = "서울, 경기도 등")
    private String dosi;
    @ApiModelProperty(required = true, value = "모임 모집 여부", example = "true/false")
    private String isOpened;
    @ApiModelProperty(required = true, value = "title, content 등 검색용", example = "댕댕이")
    private String content;
    @ApiModelProperty(required = true, value = "모임장 닉네임", example = "nickname")
    private String meetingHost;
}
