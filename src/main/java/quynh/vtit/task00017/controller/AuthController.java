package quynh.vtit.task00017.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import quynh.vtit.task00017.base.ApiResponse;
import quynh.vtit.task00017.base.RestApiV1;
import quynh.vtit.task00017.base.constant.UrlConstant;
import quynh.vtit.task00017.domain.dto.request.ForgotPasswordRequest;
import quynh.vtit.task00017.domain.dto.request.LoginRequest;
import quynh.vtit.task00017.domain.dto.request.RegisterRequest;
import quynh.vtit.task00017.domain.dto.request.ResetPasswordRequest;
import quynh.vtit.task00017.domain.dto.response.LoginResponse;
import quynh.vtit.task00017.domain.dto.response.PasswordResetTokenResponse;
import quynh.vtit.task00017.domain.dto.response.RegisterResponse;
import quynh.vtit.task00017.service.impl.AuthServiceImpl;

@RestApiV1
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authServiceImpl;

    @PostMapping(UrlConstant.AUTH.REGISTER)
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Register successfully", authServiceImpl.register(request)));
    }

    @PostMapping(UrlConstant.AUTH.LOGIN)
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Login successfully", authServiceImpl.login(request)));
    }

    @PostMapping(UrlConstant.AUTH.LOGOUT)
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal Jwt jwt) {
        authServiceImpl.logout(jwt);
        return ResponseEntity.ok(ApiResponse.ok("Logout successfully", null));
    }
//
//    @PostMapping(UrlConstant.AUTH.FORGOT_PASSWORD)
//    public ResponseEntity<ApiResponse<PasswordResetTokenResponse>> forgotPassword(
//            @Valid @RequestBody ForgotPasswordRequest request
//    ) {
//        return ResponseEntity.ok(ApiResponse.ok("Password reset token created", authServiceImpl.forgotPassword(request)));
//    }
//
//    @PostMapping(UrlConstant.AUTH.RESET_PASSWORD)
//    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
//        authServiceImpl.resetPassword(request);
//        return ResponseEntity.ok(ApiResponse.ok("Password reset successfully", null));
//    }
}
