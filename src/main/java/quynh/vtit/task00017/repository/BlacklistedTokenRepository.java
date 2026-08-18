package quynh.vtit.task00017.repository;

import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import quynh.vtit.task00017.domain.entity.BlacklistedToken;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

    boolean existsByTokenHash(String tokenHash);

    void deleteByExpiresAtBefore(Instant now);
}
