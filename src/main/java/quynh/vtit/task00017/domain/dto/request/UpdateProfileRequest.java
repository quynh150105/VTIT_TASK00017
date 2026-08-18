package quynh.vtit.task00017.domain.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(max = 20) String phone,
        @Size(max = 150) String fullName,
        String avatarUrl
) {
}
