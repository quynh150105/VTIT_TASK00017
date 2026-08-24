package quynh.vtit.task00017.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import quynh.vtit.task00017.base.enums.TransactionStatus;
import quynh.vtit.task00017.base.enums.TransactionType;
import quynh.vtit.task00017.domain.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
    select t from Transaction t
        where t.user.id = :userId
            and (:walletId is null or t.wallet.id = :walletId or t.transferWallet.id = :walletId)
            and (:type is null or t.transactionType = :type)
            and (:status is null or t.status = :status)
            and (:fromDate is null or t.transactionDate >= :fromDate)
            and (:toDate is null or t.transactionDate <= :toDate)
        order by t.transactionDate desc, t.createdAt desc
    """)
    List<Transaction> findTransactions(
            @Param("userId") Long userId,
            @Param("walletId") Long walletId,
            @Param("type") TransactionType type,
            @Param("status") TransactionStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);
}
