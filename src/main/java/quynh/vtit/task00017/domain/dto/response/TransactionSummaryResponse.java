package quynh.vtit.task00017.domain.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import quynh.vtit.task00017.base.enums.TransactionStatus;
import quynh.vtit.task00017.base.enums.TransactionType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TransactionSummaryResponse(
        Long id,
        Long walletId,
        Long categoryId,
        Long transferWalletId,
        TransactionType transactionType,
        BigDecimal amount,
        String currencyCode,
        LocalDate transactionDate,
        String title,
        TransactionStatus status
) {
}
