package petPeople.pet.controller.member.dto.req;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class MemberRegisterRequestDto {

    private String nickname;

    private String introduce;

}
