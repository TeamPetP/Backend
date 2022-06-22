package petPeople.pet.controller.meeting.dto.req;

import lombok.*;

@EqualsAndHashCode
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeetingCommentWriteReqDto {

    private String content;

}
