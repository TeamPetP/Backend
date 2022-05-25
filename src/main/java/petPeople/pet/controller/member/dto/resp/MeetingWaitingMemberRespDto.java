package petPeople.pet.controller.member.dto.resp;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class MeetingWaitingMemberRespDto {

    private Long memberId;

    private Long meetingId;

    private String nickname;

    private LocalDateTime createDate;
}
