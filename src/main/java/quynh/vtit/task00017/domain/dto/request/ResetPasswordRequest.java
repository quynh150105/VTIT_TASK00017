package quynh.vtit.task00017.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import quynh.vtit.task00017.base.constant.ErrorMessage;

public record ResetPasswordRequest(
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD) @Email(message = ErrorMessage.INVALID_FORMAT_EMAIL) String email,
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD) String token,
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD) @Size(min = 6, max = 100, message = ErrorMessage.INVALID_FORMAT_PASSWORD) String newPassword
) {
}
