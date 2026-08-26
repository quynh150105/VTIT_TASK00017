package quynh.vtit.task00017.service;

import quynh.vtit.task00017.base.enums.BudgetStatus;
import quynh.vtit.task00017.domain.dto.request.CreateBudgetRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateBudgetRequest;
import quynh.vtit.task00017.domain.dto.response.BudgetResponse;

import java.util.List;

public interface BudgetService {
    BudgetResponse createBudget(CreateBudgetRequest createBudgetRequest);
    List<BudgetResponse> getAllBudgetByUser();
//    List<BudgetResponse> getBudgetDetail();
    BudgetResponse updateBudget(Long id, UpdateBudgetRequest request);

    void deleteBudget(Long id);
}
