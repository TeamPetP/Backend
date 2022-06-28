package petPeople.pet.controller.meeting.dto.resp;

import lombok.*;

@EqualsAndHashCode
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeetingCommentWriteRespDto {

    private Long meetingCommentId;

    private Long meetingPostId;

    private Long meetingId;

    private String content;

}
