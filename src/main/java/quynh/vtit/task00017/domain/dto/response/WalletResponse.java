package quynh.vtit.task00017.domain.dto.response;

import quynh.vtit.task00017.base.enums.WalletStatus;
import quynh.vtit.task00017.base.enums.WalletType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletResponse(
     Long id,
     String name,
     Long userId,
     WalletType walletType,
     String currencyCode,
     BigDecimal openingBalance,
     BigDecimal currentBalance,
     Boolean defaultWallet,
     WalletStatus walletStatus,
     LocalDateTime createdAt,
     LocalDateTime updatedAt
) {
}
