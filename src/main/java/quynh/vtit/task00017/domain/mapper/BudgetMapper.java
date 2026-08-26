package quynh.vtit.task00017.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Service;
import quynh.vtit.task00017.domain.dto.request.UpdateBudgetRequest;
import quynh.vtit.task00017.domain.dto.response.BudgetResponse;
import quynh.vtit.task00017.domain.entity.Budget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    @Mappings({
            @Mapping(target = "userId", source = "user.id"),
            @Mapping(target = "categoryId", source = "category.id"),
            @Mapping(target = "categoryName", source= "category.name"),
            @Mapping(target="status", source = "status")
    })
    BudgetResponse toBudgetResponse(Budget budget);

    List<BudgetResponse> toBudgetResponseList(List<Budget> budgets);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "user", ignore = true),
            @Mapping(target = "category", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "currencyCode", ignore = true)
    })
    void updateBudget(UpdateBudgetRequest updateBudgetRequest, @MappingTarget Budget budget);
}
