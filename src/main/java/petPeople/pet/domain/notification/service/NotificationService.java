package petPeople.pet.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petPeople.pet.controller.member.dto.resp.notificationResp.*;
import petPeople.pet.domain.meeting.entity.vo.JoinRequestStatus;
import petPeople.pet.domain.meeting.entity.MeetingPostImage;
import petPeople.pet.domain.meeting.repository.meeting_post_Image.MeetingPostImageRepository;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.notification.entity.Notification;
import petPeople.pet.domain.notification.repository.NotificationRepository;
import petPeople.pet.domain.post.entity.PostImage;
import petPeople.pet.domain.post.repository.post_image.PostImageRepository;
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
    private final MeetingPostImageRepository meetingPostImageRepository;

    public Slice<MemberNotificationResponseDto> retrieveNotifications(Long memberId, Pageable pageable) {
        return notificationRepository.findByOwnerMemberId(pageable, memberId)
                .map(notification -> {
                    List<MeetingPostImage> meetingPostImages = new ArrayList<>();
                    List<PostImage> postImages = new ArrayList<>();
                    if (notification.getPost() != null) {
                        postImages = postImageRepository.findByPostId(notification.getPost().getId());
                    } else if(notification.getMeetingPost() != null){
                        meetingPostImages = meetingPostImageRepository.findAllMeetingPostImageByMeetingPostId(notification.getMeetingPost().getId());
                    }

                    if (notification.getComment() != null)
                        return new NotificationCommentLikeRetrieveResponseDto(notification, postImages);
                    else if (notification.getWriteComment() != null) {
                        return new NotificationCommentWriteRetrieveResponseDto(notification, postImages);
                    } else if (notification.getPost() != null) {
                        return new NotificationPostRetrieveResponseDto(notification, postImages);
                    } else if (notification.getMeetingPost() != null) {
                        return new NotificationMeetingPostLikeRetrieveResponseDto(notification, meetingPostImages);
                    } else if (notification.getMeetingWritePost() != null){
                        return new NotificationMeetingPostWriteRetrieveResponseDto(notification, meetingPostImages);
                    } else if (notification.getWriteMeetingComment() != null) {
                        return new NotificationMeetingCommentWriteRetrieveResponseDto(notification, meetingPostImages);
                    }  else if (notification.getMeetingComment() != null){
                        return new NotificationMeetingCommentRetrieveResponseDto(notification, meetingPostImages);
                    } else if (notification.getMeetingJoinRequestFlag() == JoinRequestStatus.APPROVED) {
                        return new NotificationMeetingJoinApprovedResponseDto(notification);
                    } else if (notification.getMeetingJoinRequestFlag() == JoinRequestStatus.DECLINED) {
                        return new NotificationMeetingJoinDeclinedResponseDto(notification);
                    } else {
                        return new NotificationMeetingJoinedResponseDto(notification);
                    }
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
