package quynh.vtit.task00017.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import quynh.vtit.task00017.base.RestApiV1;
import quynh.vtit.task00017.base.constant.UrlConstant;
import quynh.vtit.task00017.base.enums.TransactionStatus;
import quynh.vtit.task00017.base.enums.TransactionType;
import quynh.vtit.task00017.service.ExportService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestApiV1
@RequiredArgsConstructor
public class ExportExcelController {

    private final ExportService exportService;
    private static final DateTimeFormatter EXPORT_TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("ddMMyyyy-HHmmss");


    @GetMapping(UrlConstant.Export.TRANSACTION)
    public ResponseEntity<byte[]> exportReportTransaction(
            @RequestParam(required = false) Long walletId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) TransactionStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return exportTransactions("transaction-report", walletId, type, status, fromDate, toDate);
    }

    @GetMapping(UrlConstant.Export.RECONCILIATION_TRANSACTION)
    public ResponseEntity<byte[]> exportReconciliationReport(
            @RequestParam(required = false) Long walletId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) TransactionStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return exportTransactions("reconciliation-report", walletId, type, status, fromDate, toDate);
    }

    private ResponseEntity<byte[]> exportTransactions(
            String filenamePrefix,
            Long walletId,
            TransactionType type,
            TransactionStatus status,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        String filename = filenamePrefix + "-" + LocalDateTime.now().format(EXPORT_TIMESTAMP_FORMAT) + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(exportService.exportDataByUser(walletId, type, status, fromDate, toDate));
    }

}
