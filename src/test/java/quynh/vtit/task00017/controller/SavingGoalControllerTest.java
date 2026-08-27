package quynh.vtit.task00017.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
class SavingGoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthServiceImpl authService;

    @Test
    void savingGoalApisRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/saving-goals/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedUserCanCreateReadUpdateAndDeleteSavingGoal() throws Exception {
        authService.register(new RegisterRequest(
                "saving-goal-api-user",
                "saving-goal-api-user@example.com",
                null,
                "secret123",
                null
        ));
        String token = authService.login(new LoginRequest("saving-goal-api-user", "secret123")).accessToken();
        String walletId = createWallet(token);

        String createResponse = mockMvc.perform(post("/api/v1/saving-goals/creation")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "walletId": %s,
                                  "name": "Laptop",
                                  "targetAmount": 1000,
                                  "currencyCode": "vnd",
                                  "targetDate": "2026-12-31"
                                }
                                """.formatted(walletId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Laptop"))
                .andExpect(jsonPath("$.data.currencyCode").value("VND"))
                .andExpect(jsonPath("$.data.currentAmount").value(0))
                .andExpect(jsonPath("$.data.remainingAmount").value(1000))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String goalId = com.jayway.jsonpath.JsonPath.read(createResponse, "$.data.id").toString();

        mockMvc.perform(get("/api/v1/saving-goals/{id}", goalId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Laptop"));

        mockMvc.perform(put("/api/v1/saving-goals/{id}", goalId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "walletId": %s,
                                  "name": "Laptop Pro",
                                  "targetAmount": 1200,
                                  "currencyCode": "vnd",
                                  "targetDate": "2027-01-31"
                                }
                                """.formatted(walletId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Laptop Pro"))
                .andExpect(jsonPath("$.data.targetAmount").value(1200));

        mockMvc.perform(delete("/api/v1/saving-goals/{id}", goalId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/saving-goals/all")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    private String createWallet(String token) throws Exception {
        String response = mockMvc.perform(post("/api/v1/wallets/creation")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Saving Wallet",
                                  "walletType": "BANK",
                                  "currencyCode": "vnd",
                                  "openingBalance": 100000
                                }
                                """))
                .andReturn()
                .getResponse()
                .getContentAsString();
        return com.jayway.jsonpath.JsonPath.read(response, "$.data.id").toString();
    }
}
