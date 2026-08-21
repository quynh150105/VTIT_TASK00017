package quynh.vtit.task00017.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quynh.vtit.task00017.base.constant.ErrorMessage;
import quynh.vtit.task00017.base.enums.WalletStatus;
import quynh.vtit.task00017.domain.dto.request.CreateWalletRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateWalletRequest;
import quynh.vtit.task00017.domain.dto.response.WalletResponse;
import quynh.vtit.task00017.domain.entity.User;
import quynh.vtit.task00017.domain.entity.Wallet;
import quynh.vtit.task00017.domain.mapper.WalletMapper;
import quynh.vtit.task00017.exception.BusinessException;
import quynh.vtit.task00017.repository.UserRepository;
import quynh.vtit.task00017.repository.WalletRepository;
import quynh.vtit.task00017.service.WalletService;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final WalletMapper walletMapper;

    @Override
    @Transactional(readOnly = true)
    public List<WalletResponse> getWallets() {
        return walletMapper.toListWalletResponse(
                walletRepository.findByUserIdAndStatusOrderByCreatedAtDesc(currentUser().getId(), WalletStatus.ACTIVE)
        );
    }

    @Override
    @Transactional
    public WalletResponse createWallet(CreateWalletRequest request) {
        User user = currentUser();
        String name = request.name().trim();
        if (walletRepository.existsByUserIdAndNameIgnoreCaseAndStatus(user.getId(), name, WalletStatus.ACTIVE)) {
            throw new BusinessException(HttpStatus.CONFLICT, ErrorMessage.Wallet.ERR_WALLET_EXISTS);
        }
        Wallet wallet = walletMapper.toWallet(request);
        wallet.setName(name);
        wallet.setCurrencyCode(request.currencyCode().trim().toUpperCase());
        wallet.setCurrentBalance(request.openingBalance());
        wallet.setDefaultWallet(false);
        wallet.setStatus(WalletStatus.ACTIVE);
        wallet.setUser(user);
        walletRepository.save(wallet);
        return walletMapper.toWalletResponse(wallet);
    }

    @Override
    @Transactional
    public WalletResponse updateWallet(Long id, UpdateWalletRequest request) {
        User user = currentUser();
        Wallet wallet = findWallet(id, user.getId());
        String name = request.name().trim();
        if (walletRepository.existsByUserIdAndNameIgnoreCaseAndStatusAndIdNot(
                user.getId(), name, WalletStatus.ACTIVE, id)) {
            throw new BusinessException(HttpStatus.CONFLICT, ErrorMessage.Wallet.ERR_WALLET_EXISTS);
        }
        walletMapper.update(request, wallet);
        wallet.setName(name);
        wallet.setCurrencyCode(request.currencyCode().trim().toUpperCase());
        wallet.setCurrentBalance(request.openingBalance());
        walletRepository.save(wallet);
        return walletMapper.toWalletResponse(wallet);
    }

    @Override
    @Transactional
    public void deleteWallet(Long id) {
        Wallet wallet = findWallet(id, currentUser().getId());
        wallet.setStatus(WalletStatus.ARCHIVED);
        walletRepository.save(wallet);
    }

    private Wallet findWallet(Long id, Long userId) {
        return walletRepository.findByIdAndUserIdAndStatus(id, userId, WalletStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, ErrorMessage.Wallet.ERR_WALLET_NOT_FOUND));
    }

    private User currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED));
    }
}
