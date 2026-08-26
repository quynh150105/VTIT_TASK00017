package quynh.vtit.task00017.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quynh.vtit.task00017.base.constant.ErrorMessage;
import quynh.vtit.task00017.base.enums.BudgetStatus;
import quynh.vtit.task00017.domain.dto.request.CreateBudgetRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateBudgetRequest;
import quynh.vtit.task00017.domain.dto.response.BudgetResponse;
import quynh.vtit.task00017.domain.entity.Budget;
import quynh.vtit.task00017.domain.entity.Category;
import quynh.vtit.task00017.domain.entity.User;
import quynh.vtit.task00017.domain.mapper.BudgetMapper;
import quynh.vtit.task00017.exception.BusinessException;
import quynh.vtit.task00017.repository.BudgetRepository;
import quynh.vtit.task00017.repository.CategoryRepository;
import quynh.vtit.task00017.repository.UserRepository;
import quynh.vtit.task00017.service.BudgetService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetMapper budgetMapper;


    @Override
    @Transactional
    public BudgetResponse createBudget(CreateBudgetRequest createBudgetRequest) {
        validateDate(createBudgetRequest.startDate(), createBudgetRequest.endDate());
        User currentUser = currentUser();

        if (budgetRepository.existsByNameIgnoreCaseAndUserIdAndStatusNot(createBudgetRequest.name(), currentUser.getId(), BudgetStatus.ARCHIVED)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.Budget.ERR_BUDGET_EXISTED);
        }

        Category category = categoryRepository.findAvailableById(createBudgetRequest.categoryId(), currentUser.getId())
                .orElseThrow(()-> new BusinessException(HttpStatus.NOT_FOUND, ErrorMessage.Category.ERR_CATEGORY_NOT_FOUND));

        Budget budget = Budget.builder()
                .user(currentUser)
                .category(category)
                .name(createBudgetRequest.name())
                .limitAmount(createBudgetRequest.limitAmount())
                .currencyCode(createBudgetRequest.currencyCode().trim().toUpperCase())
                .startDate(createBudgetRequest.startDate())
                .endDate(createBudgetRequest.endDate())
                .periodType(createBudgetRequest.periodType())
                .build();

        budgetRepository.save(budget);
        return budgetMapper.toBudgetResponse(budget);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetResponse> getAllBudgetByUser() {
        User currentUser = currentUser();
        List<Budget> budgetList = budgetRepository.findByUserIdAndStatusNotOrderByCreatedAtDesc(currentUser.getId(), BudgetStatus.ARCHIVED);
        return budgetMapper.toBudgetResponseList(budgetList);
    }

    @Override
    @Transactional
    public BudgetResponse updateBudget(Long id, UpdateBudgetRequest request) {
        validateDate(request.startDate(), request.endDate());
        User currentUser = currentUser();
        Budget budget = budgetRepository.findByIdAndUserIdAndStatusNot(id, currentUser.getId(), BudgetStatus.ARCHIVED)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, ErrorMessage.Budget.ERR_BUDGET_NOT_FOUND));
        if (budgetRepository.existsByNameIgnoreCaseAndUserIdAndStatusNotAndIdNot(request.name(), currentUser.getId(), BudgetStatus.ARCHIVED, id)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.Budget.ERR_BUDGET_EXISTED);
        }
        Category category = categoryRepository.findAvailableById(request.categoryId(), currentUser.getId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, ErrorMessage.Category.ERR_CATEGORY_NOT_FOUND));
        budgetMapper.updateBudget(request, budget);
        budget.setCategory(category);
        budget.setCurrencyCode(request.currencyCode().trim().toUpperCase());
        budgetRepository.save(budget);
        return budgetMapper.toBudgetResponse(budget);
    }

    @Override
    @Transactional
    public void deleteBudget(Long id) {
        User currentUser = currentUser();
        Budget budget = budgetRepository.findByIdAndUserIdAndStatusNot(id, currentUser.getId(), BudgetStatus.ARCHIVED)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, ErrorMessage.Budget.ERR_BUDGET_NOT_FOUND));
        budget.setStatus(BudgetStatus.ARCHIVED);
        budgetRepository.save(budget);
    }

    private User currentUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new BusinessException(HttpStatus.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED));
        return user;
    }

    private void validateDate(LocalDate startDate, LocalDate endDate) {
        if(endDate.isBefore(startDate)){
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.Budget.ERR_INVALID_DATE_RANGE);
        }
    }
}
