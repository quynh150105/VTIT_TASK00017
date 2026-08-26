package quynh.vtit.task00017.domain.dto.response;

import quynh.vtit.task00017.base.enums.BudgetStatus;
import quynh.vtit.task00017.base.enums.PeriodType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BudgetResponse(
        Long id,
        Long userId,
        Long categoryId,
        String categoryName,
        String name,
        BigDecimal limitAmount,
        String currencyCode,
        LocalDate startDate,
        LocalDate endDate,
        PeriodType periodType,
        BudgetStatus status
) {
}
