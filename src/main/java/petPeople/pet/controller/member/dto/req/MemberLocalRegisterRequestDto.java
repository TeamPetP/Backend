package petPeople.pet.controller.member.dto.req;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class MemberLocalRegisterRequestDto {

    private String uid;

    private String email;

    private String name;

    private String nickname;

    private String imgUrl;

    private String introduce;

}
