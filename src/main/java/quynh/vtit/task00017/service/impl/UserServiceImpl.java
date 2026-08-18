package quynh.vtit.task00017.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import quynh.vtit.task00017.base.constant.ErrorMessage;
import quynh.vtit.task00017.domain.dto.request.ChangePasswordRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateProfileRequest;
import quynh.vtit.task00017.domain.dto.response.UserResponse;
import quynh.vtit.task00017.domain.entity.User;
import quynh.vtit.task00017.domain.mapper.UserMapper;
import quynh.vtit.task00017.exception.BusinessException;
import quynh.vtit.task00017.repository.UserRepository;
import quynh.vtit.task00017.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> userList = userRepository.findAll();
        return userMapper.toListUserResponse(userList);
    }

    @Override
    public UserResponse getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user =  userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateProfile(UpdateProfileRequest updateProfileRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED));
        if (updateProfileRequest.phone() != null
                && !updateProfileRequest.phone().equals(user.getPhone())
                && userRepository.existsByPhoneAndIdNot(updateProfileRequest.phone(), user.getId())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.User.ERR_PHONE_EXISTS);
        }
        userMapper.updateProfile(updateProfileRequest, user);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED));
        boolean check = passwordEncoder.matches(request.currentPassword(), user.getPasswordHash());
        if(!check){
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.Auth.ERR_INVALID_CREDENTIALS);
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}
