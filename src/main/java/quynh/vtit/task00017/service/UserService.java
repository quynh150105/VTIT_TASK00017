package quynh.vtit.task00017.service;

import quynh.vtit.task00017.domain.dto.request.ChangePasswordRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateProfileRequest;
import quynh.vtit.task00017.domain.dto.response.UserResponse;
import quynh.vtit.task00017.domain.entity.User;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getCurrentUser();
    UserResponse updateProfile(UpdateProfileRequest updateProfileRequest);
    void changePassword(ChangePasswordRequest request);

}
