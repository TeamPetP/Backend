package petPeople.pet.domain.meeting.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Sex {

    ALL("성별 구분 X"),
    FEMALE("여성"),
    MALE("남성");
    
    private String detail;

}
