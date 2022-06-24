package petPeople.pet.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.notification.entity.Notification;
import petPeople.pet.domain.notification.repository.NotificationRepository;
import petPeople.pet.exception.CustomException;
import petPeople.pet.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void deleteNotification(Long notificationId, Member member) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> {
                    throw new CustomException(ErrorCode.NOT_FOUND_NOTIFICATION, "알림 ID 에 맞는 알림이 없습니다.");
                });

        if (notification.getOwnerMember() != member)
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER, "해당 회원의 알림이 아닙니다.");

        notificationRepository.deleteById(notificationId);
    }
}
