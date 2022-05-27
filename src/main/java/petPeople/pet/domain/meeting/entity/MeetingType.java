package petPeople.pet.domain.meeting.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MeetingType {
    ONCE("일회성 모임"),
    REGULAR("정기적 모임");

    private String detail;
}
