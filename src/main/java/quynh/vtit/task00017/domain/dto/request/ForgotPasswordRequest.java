package quynh.vtit.task00017.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import quynh.vtit.task00017.base.constant.ErrorMessage;

public record ForgotPasswordRequest(
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD) @Email(message = ErrorMessage.INVALID_FORMAT_EMAIL) String email
) {
}
