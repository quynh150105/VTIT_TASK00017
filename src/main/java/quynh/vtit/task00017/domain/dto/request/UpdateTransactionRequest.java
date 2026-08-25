package quynh.vtit.task00017.domain.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import quynh.vtit.task00017.base.constant.ErrorMessage;
import quynh.vtit.task00017.base.enums.PaymentMethod;
import quynh.vtit.task00017.base.enums.TransactionStatus;
import quynh.vtit.task00017.base.enums.TransactionType;

public record UpdateTransactionRequest(
        @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
        Long walletId,

        @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
        Long categoryId,

        Long transferWalletId,

        @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
        TransactionType transactionType,

        @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
        @DecimalMin(value = "0.01", message = ErrorMessage.INVALID_SOME_THING_FIELD)
        BigDecimal amount,

        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD) @Size(min = 3, max = 3)
        String currencyCode,

        @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
        LocalDate transactionDate,

        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD) @Size(max = 150)
        String title,

        String note,

        @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
        PaymentMethod paymentMethod,

        @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
        TransactionStatus status
) {
}
