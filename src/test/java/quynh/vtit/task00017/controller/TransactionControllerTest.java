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
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthServiceImpl authService;

    @Test
    void transactionApisRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/transactions/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedUserCanCreateAndListTransactions() throws Exception {
        authService.register(new RegisterRequest(
                "transaction-api-user",
                "transaction-api-user@example.com",
                null,
                "secret123",
                null
        ));
        String token = authService.login(new LoginRequest("transaction-api-user", "secret123")).accessToken();
        String walletId = createWallet(token);
        String categoryId = createCategory(token);

        String transactionResponse = mockMvc.perform(post("/api/v1/transactions/creation")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "walletId": %s,
                                  "categoryId": %s,
                                  "transactionType": "EXPENSE",
                                  "amount": 30000,
                                  "currencyCode": "vnd",
                                  "transactionDate": "2026-08-21",
                                  "title": "Lunch",
                                  "paymentMethod": "CASH",
                                  "status": "POSTED"
                                }
                                """.formatted(walletId, categoryId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Lunch"))
                .andExpect(jsonPath("$.data.currencyCode").value("VND"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String transactionId = com.jayway.jsonpath.JsonPath.read(transactionResponse, "$.data.id").toString();

        mockMvc.perform(get("/api/v1/transactions/all")
                        .header("Authorization", "Bearer " + token)
                        .param("walletId", walletId)
                        .param("type", "EXPENSE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("Lunch"))
                .andExpect(jsonPath("$.data[0].walletName").doesNotExist())
                .andExpect(jsonPath("$.data[0].note").doesNotExist())
                .andExpect(jsonPath("$.data[0].createdAt").doesNotExist());

        mockMvc.perform(get("/api/v1/transactions/{id}", transactionId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Lunch"))
                .andExpect(jsonPath("$.data.walletName").value("Cash"))
                .andExpect(jsonPath("$.data.categoryName").value("Food"))
                .andExpect(jsonPath("$.data.createdAt").exists());
    }

    private String createWallet(String token) throws Exception {
        String response = mockMvc.perform(post("/api/v1/wallets/creation")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Cash",
                                  "walletType": "CASH",
                                  "currencyCode": "vnd",
                                  "openingBalance": 100000
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
