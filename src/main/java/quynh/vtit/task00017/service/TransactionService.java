package quynh.vtit.task00017.service;

import java.time.LocalDate;
import java.util.List;
import quynh.vtit.task00017.base.enums.TransactionStatus;
import quynh.vtit.task00017.base.enums.TransactionType;
import quynh.vtit.task00017.domain.dto.request.CreateTransactionRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateTransactionRequest;
import quynh.vtit.task00017.domain.dto.response.TransactionResponse;
import quynh.vtit.task00017.domain.dto.response.TransactionSummaryResponse;

public interface TransactionService {

    List<TransactionSummaryResponse> getTransactions(Long walletId, TransactionType type, TransactionStatus status,
                                                     LocalDate fromDate, LocalDate toDate);

    TransactionResponse getTransaction(Long id);

    TransactionResponse createTransaction(CreateTransactionRequest request);

    TransactionResponse updateTransaction(Long id, UpdateTransactionRequest request);

    void deleteTransaction(Long id);
}
