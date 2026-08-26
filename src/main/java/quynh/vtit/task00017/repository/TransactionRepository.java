package quynh.vtit.task00017.repository;

import java.math.BigDecimal;
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
        join fetch t.user
        join fetch t.wallet
        join fetch t.category
        left join fetch t.transferWallet
        where (:userId is null or t.user.id = :userId)
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

    @Query("""
        Select coalesce(sum(t.amount), 0)
        from Transaction t
        where t.user.id = :userId
            and t.category.id = :categoryId
                and t.transactionType = quynh.vtit.task00017.base.enums.TransactionType.EXPENSE
                    and t.status = quynh.vtit.task00017.base.enums.TransactionStatus.POSTED
                        and t.transactionDate between :startDate and :endDate
    """)
    BigDecimal sumBudgetSpent(@Param("userId") Long userId, @Param("categoryId") Long categoryId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
