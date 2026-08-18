package quynh.vtit.task00017.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import quynh.vtit.task00017.base.constant.ErrorMessage;
import quynh.vtit.task00017.base.enums.UserRole;
import quynh.vtit.task00017.base.enums.UserStatus;
import quynh.vtit.task00017.domain.dto.request.*;
import quynh.vtit.task00017.domain.dto.response.LoginResponse;
import quynh.vtit.task00017.domain.dto.response.PasswordResetTokenResponse;
import quynh.vtit.task00017.domain.dto.response.RegisterResponse;
import quynh.vtit.task00017.domain.dto.response.UserResponse;
import quynh.vtit.task00017.domain.entity.User;
import quynh.vtit.task00017.exception.BusinessException;

import java.time.LocalDateTime;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    void logout(Jwt jwt);

    PasswordResetTokenResponse forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);


}
