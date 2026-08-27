package quynh.vtit.task00017.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quynh.vtit.task00017.base.constant.ErrorMessage;
import quynh.vtit.task00017.base.enums.SavingGoalStatus;
import quynh.vtit.task00017.base.enums.WalletStatus;
import quynh.vtit.task00017.domain.dto.request.CreateGoalSavingRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateSavingGoalRequest;
import quynh.vtit.task00017.domain.dto.response.GoalSavingResponse;
import quynh.vtit.task00017.domain.entity.SavingGoal;
import quynh.vtit.task00017.domain.entity.User;
import quynh.vtit.task00017.domain.entity.Wallet;
import quynh.vtit.task00017.domain.mapper.SavingGoalMapper;
import quynh.vtit.task00017.exception.BusinessException;
import quynh.vtit.task00017.repository.SavingGoalRepository;
import quynh.vtit.task00017.repository.UserRepository;
import quynh.vtit.task00017.repository.WalletRepository;
import quynh.vtit.task00017.service.SavingGoalService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingGoalServiceImpl implements SavingGoalService{
    private final UserRepository userRepository;
    private final SavingGoalRepository savingGoalRepository;
    private final WalletRepository walletRepository;
    private final SavingGoalMapper savingGoalMapper;
    @Override
    @Transactional
    public GoalSavingResponse createGoalSaving(CreateGoalSavingRequest request) {
        User currentUser = getCurrentUser();
        String name = request.name().trim();
        if(savingGoalRepository.existsByNameIgnoreCaseAndUserIdAndStatusNot(
                name, currentUser.getId(), SavingGoalStatus.ARCHIVED
        )){
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.SavingGoal.ERR_SAVING_GOAL_EXISTS);
        }
        Wallet wallet = walletRepository
                .findByIdAndUserIdAndStatus(request.walletId(), currentUser.getId(), WalletStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, ErrorMessage.Wallet.ERR_WALLET_NOT_FOUND));

        checkCurrencyCode(wallet, request.currencyCode());

        SavingGoal savingGoal = SavingGoal.builder()
                .user(currentUser)
                .wallet(wallet)
                .name(name)
                .targetAmount(request.targetAmount())
                .targetDate(request.targetDate())
                .currencyCode(request.currencyCode().trim().toUpperCase())
                .build();

        savingGoalRepository.save(savingGoal);

        return toGoalSavingResponse(savingGoal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoalSavingResponse> getAll() {
        User currentUser = getCurrentUser();
        List<SavingGoal> savingGoalList = savingGoalRepository.findByUserIdAndStatusNotOrderByCreatedAtDesc(currentUser.getId(), SavingGoalStatus.ARCHIVED);
        return savingGoalList.stream()
                .map(this::toGoalSavingResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GoalSavingResponse getSavingGoalById(Long id) {
        User currentUser = getCurrentUser();
        SavingGoal savingGoal = savingGoalRepository.findByIdAndUserIdAndStatusNot(id, currentUser.getId(), SavingGoalStatus.ARCHIVED)
                .orElseThrow(()-> new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.SavingGoal.ERR_SAVING_GOAL_NOT_FOUND));
        return toGoalSavingResponse(savingGoal);
    }

    @Override
    @Transactional
    public GoalSavingResponse updateGoalSaving(Long id, UpdateSavingGoalRequest updateSavingGoalRequest) {
        User currentUser = getCurrentUser();

        SavingGoal savingGoal = savingGoalRepository
                .findByIdAndUserIdAndStatusNot(id, currentUser.getId(), SavingGoalStatus.ARCHIVED)
                .orElseThrow(()-> new BusinessException(HttpStatus.NOT_FOUND, ErrorMessage.SavingGoal.ERR_SAVING_GOAL_NOT_FOUND));
        String name = updateSavingGoalRequest.name().trim();

        if (savingGoalRepository.existsByNameIgnoreCaseAndUserIdAndStatusNotAndIdNot(
                name, currentUser.getId(), SavingGoalStatus.ARCHIVED, id)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.SavingGoal.ERR_SAVING_GOAL_EXISTS);
        }

        Wallet wallet = walletRepository
                .findByIdAndUserIdAndStatus(updateSavingGoalRequest.walletId(), currentUser.getId(), WalletStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, ErrorMessage.Wallet.ERR_WALLET_NOT_FOUND));
        checkCurrencyCode(wallet, updateSavingGoalRequest.currencyCode());

        savingGoalMapper.updateSavingGoal(updateSavingGoalRequest, savingGoal);
        savingGoal.setName(name);
        savingGoal.setWallet(wallet);
        savingGoal.setCurrencyCode(updateSavingGoalRequest.currencyCode().trim().toUpperCase());
        savingGoalRepository.save(savingGoal);

        return toGoalSavingResponse(savingGoal);
    }

    @Override
    @Transactional
    public void deleteGoalSavingById(Long id) {
        User currentUser = getCurrentUser();
        SavingGoal savingGoal = savingGoalRepository.
                findByIdAndUserIdAndStatusNot(id, currentUser.getId(), SavingGoalStatus.ARCHIVED)
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.SavingGoal.ERR_SAVING_GOAL_NOT_FOUND));
        savingGoal.setStatus(SavingGoalStatus.ARCHIVED);
        savingGoalRepository.save(savingGoal);
    }

    private User getCurrentUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new BusinessException(HttpStatus.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED));
        return user;
    }

    private GoalSavingResponse toGoalSavingResponse(SavingGoal savingGoal){
        BigDecimal remainingAmount = savingGoal.getTargetAmount().subtract(savingGoal.getCurrentAmount());
        BigDecimal progressPercent = savingGoal.getCurrentAmount()
                .multiply(new BigDecimal("100"))
                .divide(savingGoal.getTargetAmount(), 2, RoundingMode.HALF_UP);

        return new GoalSavingResponse(
                savingGoal.getId(),
                savingGoal.getUser().getId(),
                savingGoal.getWallet().getId(),
                savingGoal.getWallet().getName(),
                savingGoal.getName(),
                savingGoal.getTargetAmount(),
                savingGoal.getCurrentAmount(),
                remainingAmount,
                progressPercent,
                savingGoal.getCurrencyCode(),
                savingGoal.getTargetDate(),
                savingGoal.getStatus()
        );
    }

    private void checkCurrencyCode(Wallet wallet, String currencyCodeRequest){
        if(!wallet.getCurrencyCode().equalsIgnoreCase(currencyCodeRequest)){
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.SavingGoal.ERR_SAVING_GOAL_CURRENCY_MISMATCH);
        }
    }
}
