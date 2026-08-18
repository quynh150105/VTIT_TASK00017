package quynh.vtit.task00017.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import quynh.vtit.task00017.base.ApiResponse;
import quynh.vtit.task00017.base.RestApiV1;
import quynh.vtit.task00017.base.constant.UrlConstant;
import quynh.vtit.task00017.domain.dto.request.ChangePasswordRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateProfileRequest;
import quynh.vtit.task00017.domain.dto.response.UserResponse;
import quynh.vtit.task00017.service.AuthService;
import quynh.vtit.task00017.service.UserService;
import quynh.vtit.task00017.service.impl.AuthServiceImpl;

@RestApiV1
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(UrlConstant.User.GET_PROFILE)
    public ResponseEntity<ApiResponse<UserResponse>> getProfile() {
        return ResponseEntity.ok(ApiResponse.ok("Get profile successfully", userService.getCurrentUser()));
    }

    @GetMapping(UrlConstant.User.Get_All_PROFILE)
    public ResponseEntity<ApiResponse<?>> getAllProfile(){
        return ResponseEntity.ok(ApiResponse.ok("Get all profiles successfully", userService.getAllUsers()));
    }

    @PatchMapping(UrlConstant.User.UPDATE_PROFILE)
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Update profile successfully", userService.updateProfile(request)));
    }

    @PutMapping(UrlConstant.User.CHANGE_PASSWORD)
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.ok("Change password successfully", null));
    }
}
