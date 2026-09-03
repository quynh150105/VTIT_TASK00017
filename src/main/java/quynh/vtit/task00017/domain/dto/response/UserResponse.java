package quynh.vtit.task00017.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import quynh.vtit.task00017.base.enums.UserRole;
import quynh.vtit.task00017.base.enums.UserStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(
        Long id,
        String username,
        String email,
        String phone,
        String fullName,
        String avatarUrl,
        UserRole role,
        UserStatus status
) {
}
