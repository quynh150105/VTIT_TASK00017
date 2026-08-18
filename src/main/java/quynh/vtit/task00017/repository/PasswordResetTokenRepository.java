package quynh.vtit.task00017.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quynh.vtit.task00017.domain.entity.PasswordResetToken;
import quynh.vtit.task00017.domain.entity.User;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    List<PasswordResetToken> findByUserAndUsedAtIsNullAndExpiresAtAfter(User user, LocalDateTime now);
}
