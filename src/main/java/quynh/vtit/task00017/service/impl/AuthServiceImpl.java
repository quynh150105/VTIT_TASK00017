package quynh.vtit.task00017.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;

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
import quynh.vtit.task00017.base.enums.UserRole;
import quynh.vtit.task00017.base.enums.UserStatus;
import quynh.vtit.task00017.domain.dto.request.LoginRequest;
import quynh.vtit.task00017.domain.dto.request.RegisterRequest;
import quynh.vtit.task00017.domain.dto.response.LoginResponse;
import quynh.vtit.task00017.domain.dto.response.RegisterResponse;
import quynh.vtit.task00017.domain.entity.BlacklistedToken;
import quynh.vtit.task00017.domain.entity.User;
import quynh.vtit.task00017.domain.mapper.UserMapper;
import quynh.vtit.task00017.exception.BusinessException;
import quynh.vtit.task00017.repository.BlacklistedTokenRepository;
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


//    @Transactional
//    public UserResponse updateProfile(UpdateProfileRequest request) {
//        User user = currentUserService.getCurrentUser();
//        if (request.phone() != null
//                && !request.phone().equals(user.getPhone())
//                && userRepository.existsByPhoneAndIdNot(request.phone(), user.getId())) {
//            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.User.ERR_PHONE_EXISTS);
//        }
//        userMapper.updateProfile(request, user);
//        return userMapper.toResponse(user);
//    }

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
