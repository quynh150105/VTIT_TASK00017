package quynh.vtit.task00017.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quynh.vtit.task00017.base.constant.ErrorMessage;
import quynh.vtit.task00017.base.enums.TransactionStatus;
import quynh.vtit.task00017.base.enums.TransactionType;
import quynh.vtit.task00017.base.enums.UserRole;
import quynh.vtit.task00017.domain.entity.Transaction;
import quynh.vtit.task00017.domain.entity.User;
import quynh.vtit.task00017.exception.BusinessException;
import quynh.vtit.task00017.repository.TransactionRepository;
import quynh.vtit.task00017.repository.UserRepository;
import quynh.vtit.task00017.service.ExportService;

@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private static final String RECONCILIATION_TEMPLATE = "/templates/reconciliation-report-template.xlsx";

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public byte[] exportDataByUser(Long walletId,
                             TransactionType type,
                             TransactionStatus status,
                             LocalDate fromDate,
                             LocalDate toDate) {
        User user = currentUser();
        List<Transaction> listData = transactionRepository.findTransactions(
                user.getRole() == UserRole.ADMIN ? null : user.getId(), walletId, type, status, fromDate, toDate
        );

        InputStream template = getClass().getResourceAsStream(RECONCILIATION_TEMPLATE);
        if (template == null) {
            throw new IllegalStateException("Missing export template: " + RECONCILIATION_TEMPLATE);
        }

        try(template;
            Workbook workbook = new XSSFWorkbook(template);
            ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheetData = workbook.getSheetAt(0);
            String[] columns = {"id", "userName", "walletName", "categoryName", "transferWalletName", "transactionType", "amount", "currencyCode", "transactionDate", "title", "status"};

            int rowIndex = 1;
            for(Transaction data : listData){
                Row row = sheetData.createRow(rowIndex++);
                row.createCell(0).setCellValue(data.getId());
                row.createCell(1).setCellValue(userName(data.getUser()));
                row.createCell(2).setCellValue(data.getWallet().getName());
                row.createCell(3).setCellValue(data.getCategory().getName());
                row.createCell(4).setCellValue(data.getTransferWallet() == null ? "" : data.getTransferWallet().getName());
                row.createCell(5).setCellValue(data.getTransactionType().toString());
                row.createCell(6).setCellValue(data.getAmount().toString());
                row.createCell(7).setCellValue(data.getCurrencyCode());
                row.createCell(8).setCellValue(data.getTransactionDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                row.createCell(9).setCellValue(data.getTitle());
                row.createCell(10).setCellValue(data.getStatus().toString());
            }

            for(int i = 0; i< columns.length; i++){
                sheetData.autoSizeColumn(i);
            }
            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] exportDataByAdmin(Long walletId, TransactionType type, TransactionStatus status, LocalDate fromDate, LocalDate toDate) {
        return exportDataByUser(walletId, type, status, fromDate, toDate);
    }

    private User currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED));
    }

    private String userName(User user) {
        return user.getFullName() == null || user.getFullName().isBlank() ? user.getUsername() : user.getFullName();
    }
}
