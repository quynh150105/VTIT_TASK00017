package quynh.vtit.task00017.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import quynh.vtit.task00017.base.constant.ErrorMessage;
import quynh.vtit.task00017.base.enums.CategoryType;
import quynh.vtit.task00017.base.enums.PaymentMethod;
import quynh.vtit.task00017.base.enums.TransactionType;
import quynh.vtit.task00017.base.enums.WalletType;
import quynh.vtit.task00017.domain.dto.request.CreateCategoryRequest;
import quynh.vtit.task00017.domain.dto.request.CreateTransactionRequest;
import quynh.vtit.task00017.domain.dto.request.CreateWalletRequest;
import quynh.vtit.task00017.domain.dto.request.RegisterRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateTransactionRequest;
import quynh.vtit.task00017.exception.BusinessException;
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
                WalletType.MAIN,
                "vnd",
                new BigDecimal("100.00"),
                null,
                null
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
                PaymentMethod.CASH
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
                PaymentMethod.CASH
        ));

        assertThat(walletRepository.findById(wallet.id())).get()
                .extracting(value -> value.getCurrentBalance())
                .isEqualTo(new BigDecimal("90.00"));

        transactionService.deleteTransaction(created.id());

        assertThat(walletRepository.findById(wallet.id())).get()
                .extracting(value -> value.getCurrentBalance())
                .isEqualTo(new BigDecimal("100.00"));

        assertThatThrownBy(() -> transactionService.updateTransaction(created.id(), new UpdateTransactionRequest(
                wallet.id(),
                category.id(),
                null,
                TransactionType.EXPENSE,
                new BigDecimal("10.00"),
                "vnd",
                LocalDate.now(),
                "Revive",
                null,
                PaymentMethod.CASH
        )))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorMessage.Transaction.ERR_TRANSACTION_CANCELLED);
    }

    @Test
    void deletingPostedTransferRollsBackMoneyToSourceWallet() {
        authService.register(new RegisterRequest(
                "transfer-user",
                "transfer-user@example.com",
                null,
                "secret123",
                null
        ));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("transfer-user", null)
        );
        var mainWallet = walletService.createWallet(new CreateWalletRequest(
                "Main",
                WalletType.MAIN,
                "vnd",
                new BigDecimal("100.00"),
                null,
                null
        ));
        var goalWallet = walletService.createWallet(new CreateWalletRequest(
                "Goal",
                WalletType.GOAL,
                "vnd",
                BigDecimal.ZERO,
                new BigDecimal("500.00"),
                LocalDate.now().plusMonths(6)
        ));
        var category = categoryService.createCategory(new CreateCategoryRequest(
                "Goal transfer",
                CategoryType.EXPENSE,
                null,
                null
        ));

        var transfer = transactionService.createTransaction(new CreateTransactionRequest(
                mainWallet.id(),
                category.id(),
                goalWallet.id(),
                TransactionType.TRANSFER,
                new BigDecimal("30.00"),
                "vnd",
                LocalDate.now(),
                "Move to goal",
                null,
                PaymentMethod.BANK_TRANSFER
        ));

        assertThat(walletRepository.findById(mainWallet.id())).get()
                .extracting(value -> value.getCurrentBalance())
                .isEqualTo(new BigDecimal("70.00"));
        assertThat(walletRepository.findById(goalWallet.id())).get()
                .extracting(value -> value.getCurrentBalance())
                .isEqualTo(new BigDecimal("30.00"));

        transactionService.deleteTransaction(transfer.id());

        assertThat(walletRepository.findById(mainWallet.id())).get()
                .extracting(value -> value.getCurrentBalance())
                .isEqualTo(new BigDecimal("100.00"));
        assertThat(walletRepository.findById(goalWallet.id())).get()
                .extracting(value -> value.getCurrentBalance())
                .matches(value -> value.compareTo(BigDecimal.ZERO) == 0);
    }
}
