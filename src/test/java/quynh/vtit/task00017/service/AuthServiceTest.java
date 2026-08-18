package quynh.vtit.task00017.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import quynh.vtit.task00017.base.constant.ErrorMessage;
import quynh.vtit.task00017.domain.dto.request.LoginRequest;
import quynh.vtit.task00017.domain.dto.request.RegisterRequest;
import quynh.vtit.task00017.exception.BusinessException;
import quynh.vtit.task00017.service.impl.AuthServiceImpl;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthServiceImpl authServiceImpl;

    @Test
    void registerAndLoginReturnBearerToken() {
        authServiceImpl.register(new RegisterRequest(
                "quynh",
                "quynh@example.com",
                null,
                "secret123",
                "Quynh"
        ));

        var response = authServiceImpl.login(new LoginRequest("quynh", "secret123"));

        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.user().username()).isEqualTo("quynh");
    }

    @Test
    void loginRejectsWrongPassword() {
        authServiceImpl.register(new RegisterRequest(
                "wrong-pass",
                "wrong-pass@example.com",
                null,
                "secret123",
                null
        ));

        assertThatThrownBy(() -> authServiceImpl.login(new LoginRequest("wrong-pass", "bad-password")))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorMessage.Auth.ERR_INVALID_CREDENTIALS);
    }
}
