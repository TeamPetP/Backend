package petPeople.pet.domain.notification.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import petPeople.pet.domain.member.entity.QMember;
import petPeople.pet.domain.notification.entity.Notification;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static petPeople.pet.domain.member.entity.QMember.*;
import static petPeople.pet.domain.notification.entity.QNotification.notification;

@RequiredArgsConstructor
public class NotificationCustomRepositoryImpl implements NotificationCustomRepository{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public void changeNotifications(Long ownerMemberId) {
        queryFactory
                .update(notification)
                .set(notification.isChecked, true)
                .where(notification.ownerMember.id.eq(ownerMemberId))
                .execute();

        em.flush();
        em.clear();
    }

    @Override
    public long countUnReadMemberNotifications(Long memberId) {
        return queryFactory
                .select(notification.count())
                .from(notification)
                .where(notification.ownerMember.id.eq(memberId), notification.isChecked.eq(false))
                .fetchOne();
    }

    @Override
    public Optional<Notification> findByMemberIdAndPostId(Long memberId, Long postId) {
        Notification content = queryFactory
                .selectFrom(notification)
                .where(notification.member.id.eq(memberId),
                        notification.post.id.eq(postId),
                        notification.writeComment.id.isNull(),
                        notification.comment.isNull())
                .fetchOne();

        return Optional.ofNullable(content);
    }

    @Override
    public void deleteNotificationByMemberIdAndPostId(Long memberId, Long postId) {
        queryFactory
                .delete(notification)
                .where(notification.ownerMember.id.eq(memberId),
                        notification.post.id.eq(postId))
                .execute();
        em.flush();
        em.clear();
    }

    @Override
    public void deleteNotificationByOwnerMemberIdAndCommentId(Long memberId, Long commentId) {
        queryFactory
                .delete(notification)
                .where(notification.ownerMember.id.eq(memberId),
                        notification.comment.id.eq(commentId))
                .execute();
        em.flush();
        em.clear();
    }

    @Override
    public void deleteNotificationByMemberIdAndWriteCommentId(Long memberId, Long commentId) {
        queryFactory
                .delete(notification)
                .where(notification.member.id.eq(memberId),
                        notification.writeComment.id.eq(commentId))
                .execute();
        em.flush();
        em.clear();
    }


    @Override
    public Optional<Notification> findByMemberIdAndCommentId(Long memberId, Long commentId) {
        Notification content = queryFactory
                .selectFrom(notification)
                .where(notification.member.id.eq(memberId), notification.comment.id.eq(commentId))
                .fetchOne();

        return Optional.ofNullable(content);
    }

    @Override
    public Slice<Notification> findByOwnerMemberId(Pageable pageable, Long memberId) {

        List<Notification> content = queryFactory
                .selectFrom(notification)
                .join(notification.member, member).fetchJoin()
                .where(notification.ownerMember.id.eq(memberId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(notification.createdDate.desc()).fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public void deleteNotificationByMemberIdAndMeetingPostId(Long meetingPostId, Long memberId) {
        queryFactory
                .delete(notification)
                .where(notification.meetingPost.id.eq(meetingPostId)
                        .and(notification.writeMeetingComment.id.eq(meetingPostId))
                        .or(notification.ownerMember.id.eq(memberId)))
                .execute();
        em.flush();
        em.clear();
    }
}
