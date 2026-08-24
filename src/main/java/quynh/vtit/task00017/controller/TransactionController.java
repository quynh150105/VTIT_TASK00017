package quynh.vtit.task00017.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import quynh.vtit.task00017.base.ApiResponse;
import quynh.vtit.task00017.base.RestApiV1;
import quynh.vtit.task00017.base.constant.UrlConstant;
import quynh.vtit.task00017.base.enums.TransactionStatus;
import quynh.vtit.task00017.base.enums.TransactionType;
import quynh.vtit.task00017.domain.dto.request.CreateTransactionRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateTransactionRequest;
import quynh.vtit.task00017.domain.dto.response.TransactionResponse;
import quynh.vtit.task00017.domain.dto.response.TransactionSummaryResponse;
import quynh.vtit.task00017.service.TransactionService;

@RestApiV1
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping(UrlConstant.Transaction.GET_ALL)
    public ResponseEntity<ApiResponse<List<TransactionSummaryResponse>>> getTransactions(
            @RequestParam(required = false) Long walletId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) TransactionStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Get transactions successfully",
                transactionService.getTransactions(walletId, type, status, fromDate, toDate)
        ));
    }

    @GetMapping(UrlConstant.Transaction.GET_DETAIL)
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Get transaction successfully",
                transactionService.getTransaction(id)
        ));
    }

    @PostMapping(UrlConstant.Transaction.CREATE)
    public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Create transaction successfully",
                transactionService.createTransaction(request)
        ));
    }

    @PutMapping(UrlConstant.Transaction.UPDATE)
    public ResponseEntity<ApiResponse<TransactionResponse>> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTransactionRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Update transaction successfully",
                transactionService.updateTransaction(id, request)
        ));
    }

    @DeleteMapping(UrlConstant.Transaction.DELETE)
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok(ApiResponse.ok("Delete transaction successfully", null));
    }
}
