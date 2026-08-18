package quynh.vtit.task00017.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import quynh.vtit.task00017.base.constant.ErrorMessage;

public record LoginRequest(
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD) String username,
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD) String password
) {
}
