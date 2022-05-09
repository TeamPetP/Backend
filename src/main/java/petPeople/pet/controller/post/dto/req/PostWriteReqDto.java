package petPeople.pet.controller.post.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostWriteReqDto {

    private String content;

    private List<String> tagList = new ArrayList<>();

    private List<String> imgUrlList = new ArrayList<>();
}
