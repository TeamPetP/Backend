package petPeople.pet.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petPeople.pet.controller.member.dto.resp.*;
import petPeople.pet.domain.comment.repository.CommentRepository;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.notification.entity.Notification;
import petPeople.pet.domain.notification.repository.NotificationRepository;
import petPeople.pet.domain.post.entity.Post;
import petPeople.pet.domain.post.entity.PostImage;
import petPeople.pet.domain.post.repository.PostImageRepository;
import petPeople.pet.domain.post.repository.PostRepository;
import petPeople.pet.exception.CustomException;
import petPeople.pet.exception.ErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final PostImageRepository postImageRepository;

    public Slice<MemberNotificationResponseDto> retrieveNotifications(Long memberId, Pageable pageable) {
        return notificationRepository.findByOwnerMemberId(pageable, memberId)
                .map(notification -> {
                    List<PostImage> postImageList = null;
                    if (notification.getComment() != null)
                        return new NotificationCommentRetrieveResponseDto(notification, postImageList);
                    else if (notification.getPost() != null)
                        return new NotificationPostRetrieveResponseDto(notification, postImageList);
                    else if (notification.getWriteComment() != null) {
                        return new NotificationMeetingCommentWriteRetrieveResponseDto(notification);
                    } else {
                    }
                        return new NotificationCommentWriteRetrieveResponseDto(notification, postImageList);
                });
    }



    @Transactional
    public void updateNotifications(Long memberId) {
        notificationRepository.changeNotifications(memberId);
    }

    @Transactional
    public void updateNotification(Member member, Long notificationId) {
        Notification notification = validateOptionalNotification(findOptionalNotification(notificationId));
        validateAuthorizationNotification(member, notification, ErrorCode.FORBIDDEN_MEMBER);
        notification.changeIsChecked();
    }

    @Transactional
    public void deleteNotification(Long notificationId, Member member) {
        Notification notification = validateOptionalNotification(findOptionalNotification(notificationId));
        validateAuthorizationNotification(member, notification, ErrorCode.NOT_FOUND_MEMBER);
        notificationRepository.deleteById(notificationId);
    }

    @Transactional
    public void deleteAllNotification(Long memberId) {
        notificationRepository.deleteByOwnerMemberId(memberId);
    }

    public long countMemberNotReadNotifications(Long memberId) {
        return notificationRepository.countUnReadMemberNotifications(memberId);
    }

    /**
     *
     *
     */

    private Optional<Notification> findOptionalNotification(Long notificationId) {
        return notificationRepository.findById(notificationId);
    }

    private void validateAuthorizationNotification(Member member, Notification notification, ErrorCode notFoundMember) {
        if (notification.getOwnerMember() != member)
            throw new CustomException(notFoundMember, "해당 회원의 알림이 아닙니다.");
    }

    private Notification validateOptionalNotification(Optional<Notification> optionalNotification) {
        return optionalNotification.orElseThrow(() -> {
            throw new CustomException(ErrorCode.NOT_FOUND_NOTIFICATION, "알림 ID 에 맞는 알림이 없습니다.");
        });
    }
    private Long getPostId(Notification notification) {
        return notification.getPost().getId();
    }

    private List<PostImage> getPostImage(Long postId) {
        return postImageRepository.findByPostId(postId);
    }
}
