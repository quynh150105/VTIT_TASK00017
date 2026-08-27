package quynh.vtit.task00017.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quynh.vtit.task00017.base.ApiResponse;
import quynh.vtit.task00017.base.RestApiV1;
import quynh.vtit.task00017.base.constant.UrlConstant;
import quynh.vtit.task00017.domain.dto.request.CreateGoalSavingRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateSavingGoalRequest;
import quynh.vtit.task00017.service.SavingGoalService;

@RestApiV1
@RequiredArgsConstructor
public class SavingGoalController {
    private final SavingGoalService savingGoalService;

    @GetMapping(UrlConstant.SavingGoal.GET_ALL)
    public ResponseEntity<ApiResponse<?>> getAllSavingGoal(){
        return ResponseEntity.ok(
                ApiResponse.ok("get List SavingGoal successful",savingGoalService.getAll())
        );
    }

    @GetMapping(UrlConstant.SavingGoal.GET_DETAIL)
    public ResponseEntity<ApiResponse<?>> getSavingGoal(
            @PathVariable("id") Long id
    ){
        return ResponseEntity.ok(
                ApiResponse.ok(
                        "get Detail SavingGoal successful",
                        savingGoalService.getSavingGoalById(id))
        );
    }

    @PostMapping(UrlConstant.SavingGoal.CREATE)
    public ResponseEntity<ApiResponse<?>> createSavingGoal(
            @Valid @RequestBody CreateGoalSavingRequest request){
        return ResponseEntity.ok(
                ApiResponse.ok("Create SavingGoal Successful", savingGoalService.createGoalSaving(request))
        );
    }

    @PutMapping(UrlConstant.SavingGoal.UPDATE)
    public ResponseEntity<ApiResponse<?>> updateSavingGoal(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateSavingGoalRequest request){
        return ResponseEntity.ok(
                ApiResponse.ok("Update SavingGoal Successful",
                        savingGoalService.updateGoalSaving(id, request))
        );
    }

    @DeleteMapping(UrlConstant.SavingGoal.DELETE)
    public ResponseEntity<ApiResponse<?>> deleteSavingGoal(
            @PathVariable("id") Long id){
        savingGoalService.deleteGoalSavingById(id);
        return ResponseEntity.ok(
                ApiResponse.ok("Delete SavingGoal Successful", null)
        );
    }

}
