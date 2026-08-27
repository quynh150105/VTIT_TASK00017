package quynh.vtit.task00017.service;

import quynh.vtit.task00017.domain.dto.request.CreateGoalSavingRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateSavingGoalRequest;
import quynh.vtit.task00017.domain.dto.response.GoalSavingResponse;

import java.util.List;

public interface SavingGoalService {
    GoalSavingResponse createGoalSaving(CreateGoalSavingRequest createGoalSavingRequest);

    List<GoalSavingResponse> getAll();

    GoalSavingResponse updateGoalSaving(Long id, UpdateSavingGoalRequest updateSavingGoalRequest);

    void deleteGoalSavingById(Long id);

    GoalSavingResponse getSavingGoalById(Long id);

}
