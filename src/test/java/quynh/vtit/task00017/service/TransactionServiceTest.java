package quynh.vtit.task00017.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import quynh.vtit.task00017.base.enums.CategoryType;
import quynh.vtit.task00017.base.enums.PaymentMethod;
import quynh.vtit.task00017.base.enums.TransactionStatus;
import quynh.vtit.task00017.base.enums.TransactionType;
import quynh.vtit.task00017.base.enums.WalletType;
import quynh.vtit.task00017.domain.dto.request.CreateCategoryRequest;
import quynh.vtit.task00017.domain.dto.request.CreateTransactionRequest;
import quynh.vtit.task00017.domain.dto.request.CreateWalletRequest;
import quynh.vtit.task00017.domain.dto.request.RegisterRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateTransactionRequest;
import quynh.vtit.task00017.repository.WalletRepository;
import quynh.vtit.task00017.service.impl.AuthServiceImpl;

@SpringBootTest
class TransactionServiceTest {

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WalletRepository walletRepository;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void postedTransactionsUpdateWalletBalance() {
        authService.register(new RegisterRequest(
                "transaction-user",
                "transaction-user@example.com",
                null,
                "secret123",
                null
        ));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("transaction-user", null)
        );
        var wallet = walletService.createWallet(new CreateWalletRequest(
                "Cash",
                WalletType.CASH,
                "vnd",
                new BigDecimal("100.00")
        ));
        var category = categoryService.createCategory(new CreateCategoryRequest(
                "Food",
                CategoryType.EXPENSE,
                null,
                null
        ));

        var created = transactionService.createTransaction(new CreateTransactionRequest(
                wallet.id(),
                category.id(),
                null,
                TransactionType.EXPENSE,
                new BigDecimal("30.00"),
                "vnd",
                LocalDate.now(),
                "Lunch",
                null,
                PaymentMethod.CASH,
                TransactionStatus.POSTED
        ));

        assertThat(walletRepository.findById(wallet.id())).get()
                .extracting(value -> value.getCurrentBalance())
                .isEqualTo(new BigDecimal("70.00"));

        transactionService.updateTransaction(created.id(), new UpdateTransactionRequest(
                wallet.id(),
                category.id(),
                null,
                TransactionType.EXPENSE,
                new BigDecimal("10.00"),
                "vnd",
                LocalDate.now(),
                "Snack",
                null,
                PaymentMethod.CASH,
                TransactionStatus.POSTED
        ));

        assertThat(walletRepository.findById(wallet.id())).get()
                .extracting(value -> value.getCurrentBalance())
                .isEqualTo(new BigDecimal("90.00"));

        transactionService.deleteTransaction(created.id());

        assertThat(walletRepository.findById(wallet.id())).get()
                .extracting(value -> value.getCurrentBalance())
                .isEqualTo(new BigDecimal("100.00"));
    }
}
