package quynh.vtit.task00017.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import quynh.vtit.task00017.base.constant.ErrorMessage;
import quynh.vtit.task00017.base.enums.ResetTokenChannel;
import quynh.vtit.task00017.domain.dto.request.LoginRequest;
import quynh.vtit.task00017.domain.dto.request.RegisterRequest;
import quynh.vtit.task00017.domain.dto.request.ResetPasswordRequest;
import quynh.vtit.task00017.domain.entity.PasswordResetToken;
import quynh.vtit.task00017.exception.BusinessException;
import quynh.vtit.task00017.repository.PasswordResetTokenRepository;
import quynh.vtit.task00017.repository.UserRepository;
import quynh.vtit.task00017.service.impl.AuthServiceImpl;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthServiceImpl authServiceImpl;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @Test
    void wrongResetPasswordOtpIncrementsAttemptCount() {
        authServiceImpl.register(new RegisterRequest(
                "otp-user",
                "otp-user@example.com",
                null,
                "secret123",
                null
        ));
        var user = userRepository.findByEmail("otp-user@example.com").orElseThrow();
        var token = passwordResetTokenRepository.save(PasswordResetToken.builder()
                .user(user)
                .channel(ResetTokenChannel.EMAIL)
                .destination(user.getEmail())
                .optHash(passwordEncoder.encode("123456"))
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build());

        assertThatThrownBy(() -> authServiceImpl.resetPassword(new ResetPasswordRequest(
                user.getEmail(),
                "000000",
                "newSecret123"
        )))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorMessage.Auth.ERR_INVALID_OTP);

        assertThat(passwordResetTokenRepository.findById(token.getId())).get()
                .extracting(PasswordResetToken::getAttemptCount)
                .isEqualTo(1);
    }
}
