package quynh.vtit.task00017.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import quynh.vtit.task00017.base.enums.WalletStatus;
import quynh.vtit.task00017.base.enums.WalletType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WalletResponse(
     Long id,
     String name,
     Long userId,
     WalletType walletType,
     String currencyCode,
     BigDecimal openingBalance,
     BigDecimal currentBalance,
     BigDecimal targetAmount,
     LocalDate targetDate,
     Boolean defaultWallet,
     WalletStatus walletStatus,
     LocalDateTime createdAt,
     LocalDateTime updatedAt
) {
}
