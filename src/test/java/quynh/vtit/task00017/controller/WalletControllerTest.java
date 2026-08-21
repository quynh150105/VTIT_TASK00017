package quynh.vtit.task00017.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthServiceImpl authService;

    @Test
    void walletApisRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/wallets/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedUserCanCreateAndListWallets() throws Exception {
        authService.register(new RegisterRequest(
                "wallet-api-user",
                "wallet-api-user@example.com",
                null,
                "secret123",
                null
        ));
        String token = authService.login(new LoginRequest("wallet-api-user", "secret123")).accessToken();

        mockMvc.perform(post("/api/v1/wallets/creation")
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Cash"))
                .andExpect(jsonPath("$.data.currencyCode").value("VND"));

        mockMvc.perform(get("/api/v1/wallets/all").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Cash"));
    }

    @Test
    void authenticatedUserCanUpdateAndDeleteWallet() throws Exception {
        authService.register(new RegisterRequest(
                "wallet-api-crud-user",
                "wallet-api-crud-user@example.com",
                null,
                "secret123",
                null
        ));
        String token = authService.login(new LoginRequest("wallet-api-crud-user", "secret123")).accessToken();

        String createResponse = mockMvc.perform(post("/api/v1/wallets/creation")
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
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String walletId = com.jayway.jsonpath.JsonPath.read(createResponse, "$.data.id").toString();

        mockMvc.perform(put("/api/v1/wallets/{id}", walletId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Bank",
                                  "walletType": "BANK",
                                  "currencyCode": "usd",
                                  "openingBalance": 200
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Bank"))
                .andExpect(jsonPath("$.data.currencyCode").value("USD"));

        mockMvc.perform(delete("/api/v1/wallets/{id}", walletId).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/wallets/all").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
