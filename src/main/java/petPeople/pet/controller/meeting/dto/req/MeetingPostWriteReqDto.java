package petPeople.pet.controller.meeting.dto.req;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class MeetingPostWriteReqDto {

    private String title;

    private String content;

    private List<String> imgUrlList = new ArrayList<>();
}
