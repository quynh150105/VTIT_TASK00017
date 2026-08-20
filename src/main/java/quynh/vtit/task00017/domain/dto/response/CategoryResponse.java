package quynh.vtit.task00017.domain.dto.response;

import java.time.LocalDateTime;
import quynh.vtit.task00017.base.enums.CategoryType;

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
