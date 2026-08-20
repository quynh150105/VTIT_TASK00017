package quynh.vtit.task00017.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import quynh.vtit.task00017.base.ApiResponse;
import quynh.vtit.task00017.base.RestApiV1;
import quynh.vtit.task00017.base.constant.UrlConstant;
import quynh.vtit.task00017.domain.dto.request.CreateCategoryRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateCategoryRequest;
import quynh.vtit.task00017.domain.dto.response.CategoryResponse;
import quynh.vtit.task00017.service.CategoryService;

@RestApiV1
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping(UrlConstant.Category.Get_All)
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.ok("Get categories successfully", categoryService.getCategories()));
    }

    @PostMapping(UrlConstant.Category.CREATE)
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Create category successfully", categoryService.createCategory(request)));
    }

    @PutMapping(UrlConstant.Category.UPDATE)
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Update category successfully", categoryService.updateCategory(id, request)));
    }

    @DeleteMapping(UrlConstant.Category.DELETE)
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.ok("Delete category successfully", null));
    }
}
