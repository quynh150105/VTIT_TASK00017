package quynh.vtit.task00017.domain.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import quynh.vtit.task00017.base.enums.TransactionStatus;
import quynh.vtit.task00017.base.enums.TransactionType;

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
