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
import quynh.vtit.task00017.base.enums.PeriodType;
import quynh.vtit.task00017.base.enums.TransactionStatus;
import quynh.vtit.task00017.base.enums.TransactionType;
import quynh.vtit.task00017.base.enums.WalletType;
import quynh.vtit.task00017.domain.dto.request.CreateBudgetRequest;
import quynh.vtit.task00017.domain.dto.request.CreateCategoryRequest;
import quynh.vtit.task00017.domain.dto.request.CreateTransactionRequest;
import quynh.vtit.task00017.domain.dto.request.CreateWalletRequest;
import quynh.vtit.task00017.domain.dto.request.RegisterRequest;
import quynh.vtit.task00017.service.impl.AuthServiceImpl;

@SpringBootTest
class BudgetServiceTest {

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WalletService walletService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void budgetResponseIncludesPostedExpenseSpentAmount() {
        authService.register(new RegisterRequest(
                "budget-spent-user",
                "budget-spent-user@example.com",
                null,
                "secret123",
                null
        ));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("budget-spent-user", null)
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

        budgetService.createBudget(new CreateBudgetRequest(
                category.id(),
                "Food budget",
                new BigDecimal("100.00"),
                "vnd",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 31),
                PeriodType.MONTHLY
        ));
        transactionService.createTransaction(new CreateTransactionRequest(
                wallet.id(),
                category.id(),
                null,
                TransactionType.EXPENSE,
                new BigDecimal("30.00"),
                "vnd",
                LocalDate.of(2026, 8, 26),
                "Lunch",
                null,
                PaymentMethod.CASH,
                TransactionStatus.POSTED
        ));

        var budget = budgetService.getAllBudgetByUser().get(0);

        assertThat(budget.spentAmount()).isEqualByComparingTo("30.00");
        assertThat(budget.remainingAmount()).isEqualByComparingTo("70.00");
    }
}
