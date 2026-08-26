package quynh.vtit.task00017.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quynh.vtit.task00017.base.ApiResponse;
import quynh.vtit.task00017.base.RestApiV1;
import quynh.vtit.task00017.base.constant.UrlConstant;
import quynh.vtit.task00017.domain.dto.request.CreateBudgetRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateBudgetRequest;
import quynh.vtit.task00017.service.BudgetService;

@RestApiV1
@RequiredArgsConstructor
public class BudgetController {
    private final BudgetService budgetService;

    @GetMapping(UrlConstant.Budget.GET_ALL)
    public ResponseEntity<ApiResponse<?>> getAllBudgets() {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Get All budget successful",
                        budgetService.getAllBudgetByUser()));
    }

    @PostMapping(UrlConstant.Budget.CREATE)
    public ResponseEntity<ApiResponse<?>> createBudget(
            @Valid @RequestBody CreateBudgetRequest createBudgetRequest) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Create budget successful",
                        budgetService.createBudget(createBudgetRequest)));
    }

    @DeleteMapping(UrlConstant.Budget.DELETE)
    public ResponseEntity<ApiResponse<?>> deleteBudget(@PathVariable("id") Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Delete budget successful",
                        null));
    }

    @PutMapping(UrlConstant.Budget.UPDATE)
    public ResponseEntity<ApiResponse<?>> updateBudget(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBudgetRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Update budget successful",
                        budgetService.updateBudget(id, request)));
    }
}
