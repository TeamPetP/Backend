package petPeople.pet.domain.meeting.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {
    
    PICTURE("PICTURE"),      //사진 공유
    WALK("WALK"),            //산책
    VOLUNTEER("VOLUNTEER"),  //봉사
    CLASS("CLASS"),          //클래스
    TRAINING("TRAINING"),    //훈련
    AMITY("AMITY"),          //친목
    SHARE("SHARE"),          //나눔
    EVENT("EVENT"),          //이벤트
    ETC("ETC");              //기타

    private final String detail;
}
