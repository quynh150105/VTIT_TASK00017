package quynh.vtit.task00017.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quynh.vtit.task00017.base.enums.WalletStatus;
import quynh.vtit.task00017.domain.entity.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    List<Wallet> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, WalletStatus status);

    Optional<Wallet> findByIdAndUserIdAndStatus(Long id, Long userId, WalletStatus status);

    boolean existsByUserIdAndNameIgnoreCaseAndStatus(Long userId, String name, WalletStatus status);

    boolean existsByUserIdAndNameIgnoreCaseAndStatusAndIdNot(Long userId, String name, WalletStatus status, Long id);
}
