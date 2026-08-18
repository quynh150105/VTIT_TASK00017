package quynh.vtit.task00017.domain.dto.response;

public record LoginResponse(
        String accessToken,
        String tokenType,
        UserResponse user
) {
}
