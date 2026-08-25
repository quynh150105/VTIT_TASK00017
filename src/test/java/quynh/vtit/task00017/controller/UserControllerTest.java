package quynh.vtit.task00017.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import quynh.vtit.task00017.base.enums.UserRole;
import quynh.vtit.task00017.domain.dto.request.LoginRequest;
import quynh.vtit.task00017.domain.dto.request.RegisterRequest;
import quynh.vtit.task00017.repository.UserRepository;
import quynh.vtit.task00017.service.impl.AuthServiceImpl;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void userAllRequiresAdmin() throws Exception {
        String token = registerAndLogin("normal-user", "normal-user@example.com", UserRole.USER);

        mockMvc.perform(get("/api/v1/user/all").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanGetAllUsers() throws Exception {
        String token = registerAndLogin("admin-user", "admin-user@example.com", UserRole.ADMIN);

        mockMvc.perform(get("/api/v1/user/all").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    private String registerAndLogin(String username, String email, UserRole role) {
        authService.register(new RegisterRequest(username, email, null, "secret123", null));
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setRole(role);
            userRepository.save(user);
        });
        return authService.login(new LoginRequest(username, "secret123")).accessToken();
    }
}
