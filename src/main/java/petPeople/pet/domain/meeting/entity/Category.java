package petPeople.pet.domain.meeting.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {
    
    PICTURE("사진 공유"),        //사진 공유
    WALK("산책"),           //산책
    VOLUNTEER("봉사"),      //봉사
    CLASS("클래스/수업"),          //클래스
    TRAINING("교육/훈련"),       //훈련
    AMITY("친목/모임"),          //친목
    SHARE("나눔"),          //나눔
    EVENT("이벤트"),          //이벤트
    ETC("기타");             //기타

    private final String detail;
}
