package petPeople.pet.domain.notification.repository;

import com.querydsl.core.group.GroupBy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import petPeople.pet.domain.notification.entity.Notification;

import java.util.Optional;

public interface NotificationCustomRepository {
    void changeNotifications(Long memberId);

    long countUnReadMemberNotifications(Long memberId);

    Optional<Notification> findByMemberIdAndPostId(Long id, Long postId);

    void deleteNotificationByMemberIdAndPostId(Long memberId, Long postId);

    void deleteNotificationByOwnerMemberIdAndCommentId(Long memberId, Long commentId);

    void deleteNotificationByMemberIdAndWriteCommentId(Long memberId, Long commentId);

    Optional<Notification> findByMemberIdAndCommentId(Long id, Long postId);

    Slice<Notification> findByOwnerMemberId(Pageable pageable, Long memberId);
}
