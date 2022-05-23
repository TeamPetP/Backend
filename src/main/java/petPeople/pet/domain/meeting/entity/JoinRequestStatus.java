package petPeople.pet.domain.meeting.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor

public enum JoinRequestStatus {
    
    WAITING("대기중"),
    APPROVED("승인됨"),
    DECLINED("거절됨");
    
    private String status;

}
