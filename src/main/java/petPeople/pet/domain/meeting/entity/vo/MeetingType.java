package petPeople.pet.domain.meeting.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MeetingType {
    ONCE("ONCE"),
    REGULAR("REGULAR");

    private String detail;
}
