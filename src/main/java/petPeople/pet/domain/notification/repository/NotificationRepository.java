package petPeople.pet.domain.notification.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import petPeople.pet.domain.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationCustomRepository{
}
