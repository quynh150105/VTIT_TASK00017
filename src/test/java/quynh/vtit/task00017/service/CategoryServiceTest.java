package quynh.vtit.task00017.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import quynh.vtit.task00017.base.constant.ErrorMessage;
import quynh.vtit.task00017.base.enums.CategoryType;
import quynh.vtit.task00017.domain.dto.request.CreateCategoryRequest;
import quynh.vtit.task00017.domain.dto.request.RegisterRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateCategoryRequest;
import quynh.vtit.task00017.domain.entity.Category;
import quynh.vtit.task00017.exception.BusinessException;
import quynh.vtit.task00017.repository.CategoryRepository;
import quynh.vtit.task00017.service.impl.AuthServiceImpl;

@SpringBootTest
class CategoryServiceTest {

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void categoryCrudIsFlatAndOwnedByCurrentUser() {
        authService.register(new RegisterRequest(
                "category-user",
                "category-user@example.com",
                null,
                "secret123",
                null
        ));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("category-user", null)
        );

        var created = categoryService.createCategory(new CreateCategoryRequest(
                "  Food  ",
                CategoryType.EXPENSE,
                "utensils",
                "#ff0000"
        ));
        var updated = categoryService.updateCategory(created.id(), new UpdateCategoryRequest(
                "Salary",
                CategoryType.INCOME,
                null,
                "#00ff00"
        ));

        assertThat(created.name()).isEqualTo("Food");
        assertThat(created.system()).isFalse();
        assertThat(created.active()).isTrue();
        assertThat(updated.name()).isEqualTo("Salary");
        assertThat(categoryService.getCategories()).extracting("id").contains(created.id());

        categoryService.deleteCategory(created.id());

        assertThat(categoryService.getCategories()).extracting("id").doesNotContain(created.id());
    }

    @Test
    void deleteSystemCategoryReturnsSystemCategoryError() {
        authService.register(new RegisterRequest(
                "category-system-user",
                "category-system-user@example.com",
                null,
                "secret123",
                null
        ));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("category-system-user", null)
        );

        Category systemCategory = new Category();
        systemCategory.setName("System Food");
        systemCategory.setCategoryType(CategoryType.EXPENSE);
        systemCategory.setIcon("utensils");
        systemCategory.setColor("#ff0000");
        systemCategory.setUser(null);
        systemCategory.setParent(null);
        systemCategory.setSystem(true);
        systemCategory.setActive(true);
        systemCategory = categoryRepository.save(systemCategory);

        Long systemCategoryId = systemCategory.getId();
        assertThatThrownBy(() -> categoryService.deleteCategory(systemCategoryId))
                .isInstanceOfSatisfying(BusinessException.class, exception -> {
                    assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(exception.getMessage()).isEqualTo(ErrorMessage.Category.ERR_CATEGORY_IS_SYSTEM);
                });

        assertThat(categoryRepository.findById(systemCategoryId)).get()
                .extracting(Category::getActive)
                .isEqualTo(true);
    }
}
