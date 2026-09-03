package quynh.vtit.task00017.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import quynh.vtit.task00017.base.enums.WalletStatus;
import quynh.vtit.task00017.base.enums.WalletType;
import quynh.vtit.task00017.domain.dto.request.CreateWalletRequest;
import quynh.vtit.task00017.domain.dto.request.RegisterRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateWalletRequest;
import quynh.vtit.task00017.exception.BusinessException;
import quynh.vtit.task00017.repository.WalletRepository;
import quynh.vtit.task00017.service.impl.AuthServiceImpl;

@SpringBootTest
class WalletServiceTest {

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void walletCrudIsOwnedByCurrentUser() {
        authService.register(new RegisterRequest(
                "wallet-user",
                "wallet-user@example.com",
                null,
                "secret123",
                null
        ));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("wallet-user", null)
        );

        var created = walletService.createWallet(new CreateWalletRequest(
                "  Cash  ",
                WalletType.MAIN,
                "vnd",
                new BigDecimal("100000.00"),
                null,
                null
        ));
        var updated = walletService.updateWallet(created.id(), new UpdateWalletRequest(
                "Bank",
                WalletType.SPENDING,
                "usd",
                BigDecimal.ZERO
        ));

        assertThat(created.name()).isEqualTo("Cash");
        assertThat(created.currencyCode()).isEqualTo("VND");
        assertThat(created.currentBalance()).isEqualByComparingTo("100000.00");
        assertThat(created.walletStatus()).isEqualTo(WalletStatus.ACTIVE);
        assertThat(updated.name()).isEqualTo("Bank");
        assertThat(updated.currencyCode()).isEqualTo("USD");
        assertThat(walletService.getWallets()).extracting("id").contains(created.id());

        walletService.deleteWallet(created.id());

        assertThat(walletService.getWallets()).extracting("id").doesNotContain(created.id());
        assertThat(walletRepository.findById(created.id())).get()
                .extracting(wallet -> wallet.getStatus())
                .isEqualTo(WalletStatus.ARCHIVED);
    }

    @Test
    void deleteWalletWithBalanceIsRejected() {
        authService.register(new RegisterRequest(
                "wallet-balance-user",
                "wallet-balance-user@example.com",
                null,
                "secret123",
                null
        ));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("wallet-balance-user", null)
        );
        var wallet = walletService.createWallet(new CreateWalletRequest(
                "Goal",
                WalletType.GOAL,
                "vnd",
                new BigDecimal("100.00"),
                null,
                null
        ));

        assertThatThrownBy(() -> walletService.deleteWallet(wallet.id()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("exception.wallet.has.balance");
        assertThat(walletRepository.findById(wallet.id())).get()
                .extracting(value -> value.getStatus())
                .isEqualTo(WalletStatus.ACTIVE);
    }
}
