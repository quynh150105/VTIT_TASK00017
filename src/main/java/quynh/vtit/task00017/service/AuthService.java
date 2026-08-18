package quynh.vtit.task00017.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import quynh.vtit.task00017.base.constant.ErrorMessage;
import quynh.vtit.task00017.base.enums.UserRole;
import quynh.vtit.task00017.base.enums.UserStatus;
import quynh.vtit.task00017.domain.dto.request.LoginRequest;
import quynh.vtit.task00017.domain.dto.request.RegisterRequest;
import quynh.vtit.task00017.domain.dto.response.LoginResponse;
import quynh.vtit.task00017.domain.dto.response.RegisterResponse;
import quynh.vtit.task00017.domain.dto.response.UserResponse;
import quynh.vtit.task00017.domain.entity.User;
import quynh.vtit.task00017.exception.BusinessException;

import java.time.LocalDateTime;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    void logout(Jwt jwt);



//    @Transactional
//    public void changePassword(ChangePasswordRequest request) {
//        User user = currentUserService.getCurrentUser();
//        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
//            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.CURRENT_PASSWORD_INCORRECT);
//        }
//        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
//    }

//    @Transactional
//    public PasswordResetTokenResponse forgotPassword(ForgotPasswordRequest request) {
//        User user = userRepository.findByEmail(request.email())
//                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, ErrorMessage.EMAIL_NOT_FOUND));
//        String token = UUID.randomUUID().toString();
//
//        PasswordResetToken resetToken = new PasswordResetToken();
//        resetToken.setUser(user);
//        resetToken.setChannel(ResetTokenChannel.EMAIL);
//        resetToken.setDestination(user.getEmail());
//        resetToken.setTokenHash(passwordEncoder.encode(token));
//        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
//        passwordResetTokenRepository.save(resetToken);
//
//        return new PasswordResetTokenResponse(token);
////    }
//
//    @Transactional
//    public void resetPassword(ResetPasswordRequest request) {
//        User user = userRepository.findByEmail(request.email())
//                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_RESET_TOKEN));
//        PasswordResetToken resetToken = passwordResetTokenRepository
//                .findByUserAndUsedAtIsNullAndExpiresAtAfter(user, LocalDateTime.now())
//                .stream()
//                .filter(token -> passwordEncoder.matches(request.token(), token.getTokenHash()))
//                .findFirst()
//                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_RESET_TOKEN));
//
//        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
//        resetToken.setUsedAt(LocalDateTime.now());
//    }

}
