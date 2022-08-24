package petPeople.pet.domain.meeting.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JoinRequestStatus {
    
    WAITING("대기중"),
    APPROVED("승인됨"),
    DECLINED("거절됨");
    
    private String detail;
}
