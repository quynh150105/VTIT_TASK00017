package quynh.vtit.task00017.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quynh.vtit.task00017.base.constant.ErrorMessage;
import quynh.vtit.task00017.base.enums.ResetTokenChannel;
import quynh.vtit.task00017.base.enums.UserRole;
import quynh.vtit.task00017.base.enums.UserStatus;
import quynh.vtit.task00017.domain.dto.request.*;
import quynh.vtit.task00017.domain.dto.response.LoginResponse;
import quynh.vtit.task00017.domain.dto.response.PasswordResetTokenResponse;
import quynh.vtit.task00017.domain.dto.response.RegisterResponse;
import quynh.vtit.task00017.domain.entity.BlacklistedToken;
import quynh.vtit.task00017.domain.entity.PasswordResetToken;
import quynh.vtit.task00017.domain.entity.User;
import quynh.vtit.task00017.domain.mapper.UserMapper;
import quynh.vtit.task00017.exception.BusinessException;
import quynh.vtit.task00017.repository.BlacklistedTokenRepository;
import quynh.vtit.task00017.repository.PasswordResetTokenRepository;
import quynh.vtit.task00017.repository.UserRepository;
import quynh.vtit.task00017.service.AuthService;
import quynh.vtit.task00017.config.JwtTokenHash;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final JwtEncoder jwtEncoder;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final SendMailService sendMailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Transactional
    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.User.ERR_USERNAME_EXISTED);
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.User.ERR_EMAIL_EXISTED);
        }

        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        User saved = userRepository.save(user);
        return userMapper.toRegisterResponse(saved);
    }

    @Transactional
    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, ErrorMessage.Auth.ERR_INVALID_CREDENTIALS));
        if (user.getStatus() != UserStatus.ACTIVE || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, ErrorMessage.Auth.ERR_INVALID_CREDENTIALS);
        }
        user.setLastLoginAt(LocalDateTime.now());
        String token = generateToken(user);
        return new LoginResponse(token, "Bearer", userMapper.toUserResponse(user));
    }

    @Transactional
    @Override
    public void logout(Jwt jwt) {
        blacklistedTokenRepository.deleteByExpiresAtBefore(Instant.now());
        BlacklistedToken token = BlacklistedToken.builder()
                .tokenHash(JwtTokenHash.sha256(jwt.getTokenValue()))
                .expiresAt(jwt.getExpiresAt())
                .build();
        blacklistedTokenRepository.save(token);
    }

    @Override
    public PasswordResetTokenResponse forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, ErrorMessage.User.ERR_USER_NOT_EXISTED));
        int otpNumber = new Random().nextInt(900000) + 100000; // Generates a 6-digit number
        String otp = String.valueOf(otpNumber);
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .user(user)
                .channel(ResetTokenChannel.EMAIL)
                .destination(user.getEmail())
                .optHash(passwordEncoder.encode(otp))
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();
        sendMailService.sendEmail(user.getEmail(),"OTP",otp);
        passwordResetTokenRepository.save(passwordResetToken);
        return new PasswordResetTokenResponse(otp);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.User.ERR_USER_NOT_EXISTED));
        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByUserAndUsedAtIsNullAndExpiresAtAfter(user, LocalDateTime.now())
                .stream()
                .filter(token -> passwordEncoder.matches(request.otp(), token.getOptHash()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.Auth.ERR_INVALID_OTP));

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        resetToken.setUsedAt(LocalDateTime.now());
    }

    private String generateToken(User user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(15 * 60))
                .claim("role", user.getRole().name())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }


}
