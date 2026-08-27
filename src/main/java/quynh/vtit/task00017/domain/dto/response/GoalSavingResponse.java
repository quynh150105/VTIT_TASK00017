package quynh.vtit.task00017.domain.dto.response;

import quynh.vtit.task00017.base.enums.SavingGoalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalSavingResponse(
        Long id,
        Long userId,
        Long walletId,
        String walletName,
        String name,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        BigDecimal remainingAmount,
        BigDecimal progressPercent,
        String currencyCode,
        LocalDate targetDate,
        SavingGoalStatus status
) {
}
