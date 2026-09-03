package quynh.vtit.task00017.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import quynh.vtit.task00017.base.enums.UserRole;
import quynh.vtit.task00017.domain.dto.request.LoginRequest;
import quynh.vtit.task00017.domain.dto.request.RegisterRequest;
import quynh.vtit.task00017.repository.UserRepository;
import quynh.vtit.task00017.service.impl.AuthServiceImpl;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private UserRepository userRepository;

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

        mockMvc.perform(get("/api/v1/export/transactions")
                        .header("Authorization", "Bearer " + token)
                        .param("walletId", walletId)
                        .param("type", "EXPENSE"))
                .andExpect(status().isOk());
    }

    @Test
    void transferFromMainToGoalUpdatesGoalCurrentBalance() throws Exception {
        authService.register(new RegisterRequest(
                "transaction-transfer-api-user",
                "transaction-transfer-api-user@example.com",
                null,
                "secret123",
                null
        ));
        String token = authService.login(new LoginRequest("transaction-transfer-api-user", "secret123")).accessToken();
        String mainWalletId = createWallet(token);
        String goalWalletId = createGoalWallet(token);
        String categoryId = createCategory(token);

        mockMvc.perform(post("/api/v1/transactions/creation")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "walletId": %s,
                                  "categoryId": %s,
                                  "transferWalletId": %s,
                                  "transactionType": "TRANSFER",
                                  "amount": 30000,
                                  "currencyCode": "vnd",
                                  "transactionDate": "2026-08-21",
                                  "title": "Move to goal",
                                  "paymentMethod": "BANK_TRANSFER",
                                  "status": "POSTED"
                                }
                                """.formatted(mainWalletId, categoryId, goalWalletId)))
                .andExpect(status().isOk());

        String walletsResponse = mockMvc.perform(get("/api/v1/wallets/all")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<Number> goalBalances = com.jayway.jsonpath.JsonPath.read(
                walletsResponse,
                "$.data[?(@.id == %s)].currentBalance".formatted(goalWalletId)
        );

        assertThat(new BigDecimal(goalBalances.get(0).toString())).isEqualByComparingTo("30000");
    }

    @Test
    void adminCanExportTransactions() throws Exception {
        String userToken = registerAndLogin("transaction-owner-user", "transaction-owner-user@example.com", UserRole.USER);
        String walletId = createWallet(userToken);
        String categoryId = createCategory(userToken);
        String transactionId = createTransaction(userToken, walletId, categoryId, "Admin visible lunch");
        String adminToken = registerAndLogin("transaction-admin-user", "transaction-admin-user@example.com", UserRole.ADMIN);

        mockMvc.perform(get("/api/v1/transactions/all").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].id", hasItem(Integer.parseInt(transactionId))));

        mockMvc.perform(get("/api/v1/transactions/{id}", transactionId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Admin visible lunch"));

        byte[] exportBytes = mockMvc.perform(get("/api/v1/export/transactions")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(result -> assertThat(result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION))
                        .matches("attachment; filename=\"transaction-report-\\d{8}-\\d{6}\\.xlsx\""))
                .andReturn()
                .getResponse()
                .getContentAsByteArray();

        assertThat(exportBytes).isNotEmpty();
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(exportBytes))) {
            assertThat(workbook.getSheetAt(0).getRow(0).getCell(1).getStringCellValue()).isEqualTo("userName");
            assertThat(workbook.getSheetAt(0).getRow(0).getCell(2).getStringCellValue()).isEqualTo("walletName");
            assertThat(workbook.getSheetAt(0).getRow(0).getCell(3).getStringCellValue()).isEqualTo("categoryName");
            assertThat(workbook.getSheetAt(0).getRow(0).getCell(4).getStringCellValue()).isEqualTo("transferWalletName");
            assertThat(workbook.getSheetAt(0)).anySatisfy(row ->
                    assertThat(row.getCell(9).getStringCellValue()).isEqualTo("Admin visible lunch")
            );
        }
    }

    private String registerAndLogin(String username, String email, UserRole role) {
        authService.register(new RegisterRequest(username, email, null, "secret123", null));
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setRole(role);
            userRepository.save(user);
        });
        return authService.login(new LoginRequest(username, "secret123")).accessToken();
    }

    private String createTransaction(String token, String walletId, String categoryId, String title) throws Exception {
        String response = mockMvc.perform(post("/api/v1/transactions/creation")
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
                                  "title": "%s",
                                  "paymentMethod": "CASH",
                                  "status": "POSTED"
                                }
                                """.formatted(walletId, categoryId, title)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return com.jayway.jsonpath.JsonPath.read(response, "$.data.id").toString();
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
                                  "openingBalance": 100000
                                }
                                """))
                .andReturn()
                .getResponse()
                .getContentAsString();
        return com.jayway.jsonpath.JsonPath.read(response, "$.data.id").toString();
    }

    private String createGoalWallet(String token) throws Exception {
        String response = mockMvc.perform(post("/api/v1/wallets/creation")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Goal",
                                  "walletType": "GOAL",
                                  "currencyCode": "vnd",
                                  "openingBalance": 0,
                                  "targetAmount": 100000
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
