package quynh.vtit.task00017.domain.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import quynh.vtit.task00017.base.enums.PaymentMethod;
import quynh.vtit.task00017.base.enums.TransactionStatus;
import quynh.vtit.task00017.base.enums.TransactionType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TransactionResponse(
        Long id,
        Long userId,
        Long walletId,
        String walletName,
        Long categoryId,
        String categoryName,
        Long transferWalletId,
        String transferWalletName,
        TransactionType transactionType,
        BigDecimal amount,
        String currencyCode,
        LocalDate transactionDate,
        String title,
        String note,
        PaymentMethod paymentMethod,
        TransactionStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
