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
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthServiceImpl authService;

    @Test
    void categoryApisRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/categories/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedUserCanCreateAndListCategories() throws Exception {
        authService.register(new RegisterRequest(
                "category-api-user",
                "category-api-user@example.com",
                null,
                "secret123",
                null
        ));
        String token = authService.login(new LoginRequest("category-api-user", "secret123")).accessToken();

        mockMvc.perform(post("/api/v1/categories/creation")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Food",
                                  "categoryType": "EXPENSE",
                                  "icon": "utensils",
                                  "color": "#ff0000"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Food"));

        mockMvc.perform(get("/api/v1/categories/all").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Food"));
    }
}
