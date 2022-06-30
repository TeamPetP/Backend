package petPeople.pet.controller.member.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class MemberCountDto {

    private Long postCnt;
    private Long meetingBookCnt;
    private Long notificationCnt;

}
