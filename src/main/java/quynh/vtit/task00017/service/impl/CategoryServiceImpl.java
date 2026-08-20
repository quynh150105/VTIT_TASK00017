package quynh.vtit.task00017.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quynh.vtit.task00017.base.constant.ErrorMessage;
import quynh.vtit.task00017.domain.dto.request.CreateCategoryRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateCategoryRequest;
import quynh.vtit.task00017.domain.dto.response.CategoryResponse;
import quynh.vtit.task00017.domain.entity.Category;
import quynh.vtit.task00017.domain.entity.User;
import quynh.vtit.task00017.domain.mapper.CategoryMapper;
import quynh.vtit.task00017.exception.BusinessException;
import quynh.vtit.task00017.repository.CategoryRepository;
import quynh.vtit.task00017.repository.UserRepository;
import quynh.vtit.task00017.service.CategoryService;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories() {
        return categoryMapper.toResponses(categoryRepository.findCategoriesAvailable(currentUser().getId()));
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        User user = currentUser();
        String name = request.name().trim();
        if (categoryRepository.existsByUserIdAndNameIgnoreCaseAndCategoryTypeAndActiveTrue(
                user.getId(), name, request.categoryType())) {
            throw new BusinessException(HttpStatus.CONFLICT, ErrorMessage.Category.ERR_CATEGORY_EXISTS);
        }
        Category category = categoryMapper.toEntity(request);
        category.setName(name);
        category.setUser(user);
        category.setParent(null);
        category.setSystem(false);
        category.setActive(true);
        categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        User user = currentUser();
        Category category = findCategory(id, user.getId());
        String name = request.name().trim();
        if (categoryRepository.existsByUserIdAndNameIgnoreCaseAndCategoryTypeAndActiveTrueAndIdNot(
                user.getId(), name, request.categoryType(), id)) {
            throw new BusinessException(HttpStatus.CONFLICT, ErrorMessage.Category.ERR_CATEGORY_EXISTS);
        }
        if(checkIsSystem(category)){
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.Category.ERR_CATEGORY_IS_SYSTEM);
        }
        categoryMapper.update(request, category);
        category.setName(name);
        category.setParent(null);
        categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = findAvailableCategory(id, currentUser().getId());
        if(checkIsSystem(category)){
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.Category.ERR_CATEGORY_IS_SYSTEM);
        }
        category.setActive(false);
        categoryRepository.save(category);
    }

    private Category findCategory(Long id, Long userId) {
        return categoryRepository.findByIdAndUserIdAndActiveTrue(id, userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, ErrorMessage.Category.ERR_CATEGORY_NOT_FOUND));
    }

    private Category findAvailableCategory(Long id, Long userId) {
        return categoryRepository.findAvailableById(id, userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, ErrorMessage.Category.ERR_CATEGORY_NOT_FOUND));
    }

    private User currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED));
    }

    private boolean checkIsSystem(Category category){
        return Boolean.TRUE.equals(category.getSystem());
    }
}
