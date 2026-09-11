package quynh.vtit.task00017.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quynh.vtit.task00017.base.constant.ErrorMessage;
import quynh.vtit.task00017.base.enums.TransactionStatus;
import quynh.vtit.task00017.base.enums.TransactionType;
import quynh.vtit.task00017.base.enums.UserRole;
import quynh.vtit.task00017.base.enums.WalletStatus;
import quynh.vtit.task00017.domain.dto.request.CreateTransactionRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateTransactionRequest;
import quynh.vtit.task00017.domain.dto.response.TransactionResponse;
import quynh.vtit.task00017.domain.dto.response.TransactionSummaryResponse;
import quynh.vtit.task00017.domain.entity.Category;
import quynh.vtit.task00017.domain.entity.Transaction;
import quynh.vtit.task00017.domain.entity.User;
import quynh.vtit.task00017.domain.entity.Wallet;
import quynh.vtit.task00017.domain.mapper.TransactionMapper;
import quynh.vtit.task00017.exception.BusinessException;
import quynh.vtit.task00017.repository.CategoryRepository;
import quynh.vtit.task00017.repository.TransactionRepository;
import quynh.vtit.task00017.repository.UserRepository;
import quynh.vtit.task00017.repository.WalletRepository;
import quynh.vtit.task00017.service.TransactionService;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TransactionSummaryResponse> getTransactions(
            Long walletId,
            TransactionType type,
            TransactionStatus status,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        User user = currentUser();
        return transactionMapper.toSummaryResponses(
                transactionRepository.findTransactions(readableUserId(user), walletId, type, status, fromDate, toDate)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransaction(Long id) {
        User user = currentUser();
        return transactionMapper.toResponse(
                user.getRole() == UserRole.ADMIN ? findTransaction(id) : findTransaction(id, user.getId())
        );
    }

    @Override
    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        User user = currentUser();
        Transaction transaction = transactionMapper.toEntity(request);
        String currencyCode = request.currencyCode().trim().toUpperCase();
        fillTransaction(transaction, user.getId(), request.walletId(), request.categoryId(), request.transferWalletId(), currencyCode);
        transaction.setUser(user);
        transaction.setTitle(request.title().trim());
        transaction.setCurrencyCode(currencyCode);
        transaction.setStatus(TransactionStatus.POSTED);
        applyBalance(transaction, false);
        transactionRepository.save(transaction);
        return transactionMapper.toResponse(transaction);
    }

    @Override
    @Transactional
    public TransactionResponse updateTransaction(Long id, UpdateTransactionRequest request) {
        User user = currentUser();
        Transaction transaction = findTransaction(id, user.getId());
        if(transaction.getStatus() == TransactionStatus.CANCELLED) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    ErrorMessage.Transaction.ERR_TRANSACTION_CANCELLED
            );
        }
        lockTransactionWallets(transaction, user.getId());
        applyBalance(transaction, true);
        transactionMapper.update(request, transaction);
        String currencyCode = request.currencyCode().trim().toUpperCase();
        fillTransaction(transaction, user.getId(), request.walletId(), request.categoryId(), request.transferWalletId(), currencyCode);
        transaction.setTitle(request.title().trim());
        transaction.setCurrencyCode(currencyCode);
        transaction.setStatus(TransactionStatus.POSTED);
        applyBalance(transaction, false);
        transactionRepository.save(transaction);
        return transactionMapper.toResponse(transaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id) {
        User user = currentUser();
        Transaction transaction = findTransaction(id, user.getId());
        lockTransactionWallets(transaction, user.getId());
        applyBalance(transaction, true);
        transaction.setStatus(TransactionStatus.CANCELLED);
        transactionRepository.save(transaction);
    }

    private void fillTransaction(
            Transaction transaction,
            Long userId,
            Long walletId,
            Long categoryId,
            Long transferWalletId,
            String currencyCode
    ) {
        Wallet wallet;
        Category category = findCategory(categoryId, userId);
        Wallet transferWallet = null;
        if (transaction.getTransactionType() == TransactionType.TRANSFER) {
            if (transferWalletId == null) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.Transaction.ERR_TRANSFER_WALLET_REQUIRED);
            }
            if (walletId.equals(transferWalletId)) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.Transaction.ERR_TRANSFER_WALLET_SAME);
            }
            if (walletId.compareTo(transferWalletId) < 0) {
                wallet = findWalletForUpdate(walletId, userId);
                transferWallet = findWalletForUpdate(transferWalletId, userId);
            } else {
                transferWallet = findWalletForUpdate(transferWalletId, userId);
                wallet = findWalletForUpdate(walletId, userId);
            }
        } else {
            wallet = findWalletForUpdate(walletId, userId);
        }
        if (!wallet.getCurrencyCode().equalsIgnoreCase(currencyCode)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.Transaction.ERR_TRANSACTION_CURRENCY_MISMATCH);
        }
        if (transaction.getTransactionType() == TransactionType.TRANSFER) {
            if (!wallet.getCurrencyCode().equalsIgnoreCase(transferWallet.getCurrencyCode())) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.Transaction.ERR_TRANSFER_CURRENCY_MISMATCH);
            }
        }
        transaction.setWallet(wallet);
        transaction.setCategory(category);
        transaction.setTransferWallet(transferWallet);
    }

    private void lockTransactionWallets(Transaction transaction, Long userId) {
        Wallet wallet = transaction.getWallet();
        Wallet transferWallet = transaction.getTransferWallet();
        if (transaction.getTransactionType() == TransactionType.TRANSFER && transferWallet != null) {
            if (wallet.getId().compareTo(transferWallet.getId()) < 0) {
                transaction.setWallet(findWalletForUpdate(wallet.getId(), userId));
                transaction.setTransferWallet(findWalletForUpdate(transferWallet.getId(), userId));
            } else {
                transaction.setTransferWallet(findWalletForUpdate(transferWallet.getId(), userId));
                transaction.setWallet(findWalletForUpdate(wallet.getId(), userId));
            }
            return;
        }
        transaction.setWallet(findWalletForUpdate(wallet.getId(), userId));
    }

    private void applyBalance(Transaction transaction, boolean reverse) {
        if (transaction.getStatus() != TransactionStatus.POSTED) {
            return;
        }
        BigDecimal amount = reverse ? transaction.getAmount().negate() : transaction.getAmount();
        if (transaction.getTransactionType() == TransactionType.INCOME) {
            add(transaction.getWallet(), amount);
        } else if (transaction.getTransactionType() == TransactionType.EXPENSE) {
            add(transaction.getWallet(), amount.negate());
        } else {
            add(transaction.getWallet(), amount.negate());
            add(transaction.getTransferWallet(), amount);
        }
    }

    private void add(Wallet wallet, BigDecimal amount) {
        BigDecimal newBalance = wallet.getCurrentBalance().add(amount);
        if(newBalance.compareTo(BigDecimal.ZERO) < 0){
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.Transaction.ERR_INSUFFICIENT_BALANCE);
        }
        wallet.setCurrentBalance(newBalance);
    }

    private Transaction findTransaction(Long id, Long userId) {
        return transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, ErrorMessage.Transaction.ERR_TRANSACTION_NOT_FOUND));
    }

    private Transaction findTransaction(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, ErrorMessage.Transaction.ERR_TRANSACTION_NOT_FOUND));
    }

    private Long readableUserId(User user) {
        return user.getRole() == UserRole.ADMIN ? null : user.getId();
    }

    private Category findCategory(Long id, Long userId) {
        return categoryRepository.findAvailableById(id, userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, ErrorMessage.Category.ERR_CATEGORY_NOT_FOUND));
    }

    private User currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED));
    }

    private Wallet findWalletForUpdate(Long id, Long userId){
        return walletRepository.findByIdAndUserIdAndStatusForUpdate(id, userId, WalletStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, ErrorMessage.Wallet.ERR_WALLET_NOT_FOUND));
    }
}
