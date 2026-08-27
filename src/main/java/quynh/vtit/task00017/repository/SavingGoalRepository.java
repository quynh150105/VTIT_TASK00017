package quynh.vtit.task00017.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quynh.vtit.task00017.base.enums.SavingGoalStatus;
import quynh.vtit.task00017.domain.entity.SavingGoal;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavingGoalRepository extends JpaRepository<SavingGoal, Long> {

    List<SavingGoal> findByUserIdAndStatusNotOrderByCreatedAtDesc(Long userId, SavingGoalStatus status);

    Optional<SavingGoal> findByIdAndUserIdAndStatusNot(Long id, Long userId, SavingGoalStatus status);

    boolean existsByNameIgnoreCaseAndUserIdAndStatusNot(String name, Long userId, SavingGoalStatus status);

    boolean existsByNameIgnoreCaseAndUserIdAndStatusNotAndIdNot(String name, Long userId, SavingGoalStatus status, Long id);

}
