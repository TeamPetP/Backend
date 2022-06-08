package petPeople.pet.controller.meeting.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingImageRetrieveRespDto {

    private Long meetingImageId;

    private Long meetingId;

    private String imgUrl;

}
