package petPeople.pet.domain.notification.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import petPeople.pet.domain.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationCustomRepository{

    @Modifying(clearAutomatically = true)
    @Query("delete from Notification n where n.ownerMember.id = :memberId")
    void deleteByOwnerMemberId(@Param("memberId") Long memberId);
}
