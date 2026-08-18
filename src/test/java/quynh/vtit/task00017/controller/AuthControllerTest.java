package quynh.vtit.task00017.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.web.servlet.MockMvc;
import quynh.vtit.task00017.base.constant.ErrorMessage;
import quynh.vtit.task00017.domain.dto.request.LoginRequest;
import quynh.vtit.task00017.domain.dto.request.RegisterRequest;
import quynh.vtit.task00017.service.impl.AuthServiceImpl;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Test
    void logoutRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutReturnsSuccessForAuthenticatedUser() throws Exception {
        authService.register(new RegisterRequest(
                "logout-user",
                "logout@example.com",
                null,
                "secret123",
                null
        ));
        String token = authService.login(new LoginRequest("logout-user", "secret123")).accessToken();

        mockMvc.perform(post("/api/v1/auth/logout").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Logout successfully"));

        mockMvc.perform(post("/api/v1/auth/logout").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ErrorMessage.Auth.ERR_TOKEN_ALREADY_INVALIDATED));
    }

    @Test
    void expiredTokenReturnsSpecificError() throws Exception {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject("expired-user")
                .issuedAt(now.minusSeconds(180))
                .expiresAt(now.minusSeconds(120))
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),
                claims
        )).getTokenValue();

        mockMvc.perform(post("/api/v1/auth/logout").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ErrorMessage.Auth.ERR_TOKEN_EXPIRED));
    }

    @Test
    void wrongUsernameAndPasswordReturnSameError() throws Exception {
        authService.register(new RegisterRequest(
                "credential-user",
                "credential@example.com",
                null,
                "secret123",
                null
        ));

        assertInvalidCredentials("missing-user", "secret123");
        assertInvalidCredentials("credential-user", "wrong-password");
    }

    private void assertInvalidCredentials(String username, String password) throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ErrorMessage.Auth.ERR_INVALID_CREDENTIALS));
    }
}
