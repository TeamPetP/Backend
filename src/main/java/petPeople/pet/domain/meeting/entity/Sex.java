package petPeople.pet.domain.meeting.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Sex {

    ALL("ALL"),
    FEMALE("FEMALE"),
    MALE("MALE");
    
    private String detail;

}
