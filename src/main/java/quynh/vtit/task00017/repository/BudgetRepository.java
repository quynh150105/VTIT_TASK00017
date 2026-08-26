package quynh.vtit.task00017.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quynh.vtit.task00017.base.enums.BudgetStatus;
import quynh.vtit.task00017.domain.entity.Budget;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget,Long> {
    List<Budget> findByUserIdAndStatusNotOrderByCreatedAtDesc(Long userId, BudgetStatus status);

    Optional<Budget> findByIdAndUserIdAndStatusNot(Long id, Long userId, BudgetStatus status);

    boolean existsByNameIgnoreCaseAndUserIdAndStatusNot(String name, Long userId, BudgetStatus status);

    boolean existsByNameIgnoreCaseAndUserIdAndStatusNotAndIdNot(String name, Long userId, BudgetStatus status, Long id);
}
