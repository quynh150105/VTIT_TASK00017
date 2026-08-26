package quynh.vtit.task00017.domain.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import quynh.vtit.task00017.base.constant.ErrorMessage;
import quynh.vtit.task00017.base.enums.PeriodType;

public record UpdateBudgetRequest(
        @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
        Long categoryId,

        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        @Size(max = 100)
        String name,

        @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
        @DecimalMin(value = "0.01", message = ErrorMessage.INVALID_SOME_THING_FIELD)
        BigDecimal limitAmount,

        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        @Size(min = 3, max = 3)
        String currencyCode,

        @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
        LocalDate startDate,

        @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
        LocalDate endDate,

        @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
        PeriodType periodType
) {
}
