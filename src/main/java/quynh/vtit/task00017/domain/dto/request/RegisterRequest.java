package quynh.vtit.task00017.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import quynh.vtit.task00017.base.constant.ErrorMessage;

public record RegisterRequest(
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD) @Size(max = 100)
        String username,
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD) @Email(message = ErrorMessage.INVALID_FORMAT_EMAIL) @Size(max = 150)
        String email,
        @Size(max = 20)
        String phone,
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD) @Size(min = 6, max = 100, message = ErrorMessage.INVALID_FORMAT_PASSWORD)
        String password,
        @Size(max = 150) String fullName
) {
}
