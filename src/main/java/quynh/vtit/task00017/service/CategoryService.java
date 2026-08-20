package quynh.vtit.task00017.service;

import java.util.List;
import quynh.vtit.task00017.domain.dto.request.CreateCategoryRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateCategoryRequest;
import quynh.vtit.task00017.domain.dto.response.CategoryResponse;

public interface CategoryService {

    List<CategoryResponse> getCategories();

    CategoryResponse createCategory(CreateCategoryRequest request);

    CategoryResponse updateCategory(Long id, UpdateCategoryRequest request);

    void deleteCategory(Long id);
}
