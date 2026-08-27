package quynh.vtit.task00017.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import quynh.vtit.task00017.domain.dto.request.UpdateSavingGoalRequest;
import quynh.vtit.task00017.domain.entity.SavingGoal;

@Mapper(componentModel = "spring")
public interface SavingGoalMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target="user", ignore = true),
            @Mapping(target="wallet", ignore = true),
            @Mapping(target = "currentAmount", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target="currencyCode", ignore = true)
    })
    void updateSavingGoal(UpdateSavingGoalRequest updateSavingGoalRequest, @MappingTarget SavingGoal savingGoal);
}
