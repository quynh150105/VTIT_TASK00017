package quynh.vtit.task00017.service;

import quynh.vtit.task00017.base.enums.TransactionStatus;
import quynh.vtit.task00017.base.enums.TransactionType;

import java.time.LocalDate;

public interface ExportService {
    byte[] exportDataByUser(Long walletId,
                      TransactionType type,
                      TransactionStatus status,
                      LocalDate fromDate,
                      LocalDate toDate);

    byte[] exportDataByAdmin(Long walletId,
                             TransactionType type,
                             TransactionStatus status,
                             LocalDate fromDate,
                             LocalDate toDate);
}
