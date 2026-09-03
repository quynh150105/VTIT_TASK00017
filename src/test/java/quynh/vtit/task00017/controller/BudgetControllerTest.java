package quynh.vtit.task00017.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import quynh.vtit.task00017.domain.dto.request.LoginRequest;
import quynh.vtit.task00017.domain.dto.request.RegisterRequest;
import quynh.vtit.task00017.service.impl.AuthServiceImpl;

@SpringBootTest
@AutoConfigureMockMvc
class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthServiceImpl authService;

    @Test
    void budgetListIncludesPostedExpenseSpentAmount() throws Exception {
        authService.register(new RegisterRequest(
                "budget-api-user",
                "budget-api-user@example.com",
                null,
                "secret123",
                null
        ));
        String token = authService.login(new LoginRequest("budget-api-user", "secret123")).accessToken();
        String walletId = createWallet(token);
        String categoryId = createCategory(token);

        mockMvc.perform(post("/api/v1/budgets/creation")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "categoryId": %s,
                                  "name": "Food budget",
                                  "limitAmount": 100,
                                  "currencyCode": "vnd",
                                  "startDate": "2026-08-01",
                                  "endDate": "2026-08-31",
                                  "periodType": "MONTHLY"
                                }
                                """.formatted(categoryId)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/transactions/creation")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "walletId": %s,
                                  "categoryId": %s,
                                  "transactionType": "EXPENSE",
                                  "amount": 30,
                                  "currencyCode": "vnd",
                                  "transactionDate": "2026-08-26",
                                  "title": "Lunch",
                                  "paymentMethod": "CASH",
                                  "status": "POSTED"
                                }
                                """.formatted(walletId, categoryId)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/budgets/all")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].spentAmount").value(30.0))
                .andExpect(jsonPath("$.data[0].remainingAmount").value(70.0));
    }

    private String createWallet(String token) throws Exception {
        String response = mockMvc.perform(post("/api/v1/wallets/creation")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Cash",
                                  "walletType": "MAIN",
                                  "currencyCode": "vnd",
                                  "openingBalance": 100
                                }
                                """))
                .andReturn()
                .getResponse()
                .getContentAsString();
        return com.jayway.jsonpath.JsonPath.read(response, "$.data.id").toString();
    }

    private String createCategory(String token) throws Exception {
        String response = mockMvc.perform(post("/api/v1/categories/creation")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Food",
                                  "categoryType": "EXPENSE"
                                }
                                """))
                .andReturn()
                .getResponse()
                .getContentAsString();
        return com.jayway.jsonpath.JsonPath.read(response, "$.data.id").toString();
    }
}
