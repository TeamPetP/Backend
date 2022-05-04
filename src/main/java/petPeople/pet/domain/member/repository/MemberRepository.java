package petPeople.pet.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petPeople.pet.domain.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUid(String uid);
}
