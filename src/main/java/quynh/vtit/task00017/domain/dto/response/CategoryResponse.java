package quynh.vtit.task00017.domain.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import quynh.vtit.task00017.base.enums.CategoryType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CategoryResponse(
        Long id,
        String name,
        CategoryType categoryType,
        String icon,
        String color,
        Boolean system,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
