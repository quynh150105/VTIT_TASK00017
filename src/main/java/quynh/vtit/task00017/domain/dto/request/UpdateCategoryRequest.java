package quynh.vtit.task00017.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import quynh.vtit.task00017.base.constant.ErrorMessage;
import quynh.vtit.task00017.base.enums.CategoryType;

public record UpdateCategoryRequest(
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD) @Size(max = 100)
        String name,
        @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
        CategoryType categoryType,
        @Size(max = 50) String icon,
        @Size(max = 30) String color
) {
}
