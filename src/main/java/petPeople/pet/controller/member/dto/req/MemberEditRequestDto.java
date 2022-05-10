package petPeople.pet.controller.member.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEditRequestDto {

    private String nickname;
    private String introduce;

}
