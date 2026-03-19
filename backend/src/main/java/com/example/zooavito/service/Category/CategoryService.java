package com.example.zooavito.service.Category;

import com.example.zooavito.request.CategoryOrderRequest;
import com.example.zooavito.request.CategoryRequest;
import com.example.zooavito.response.CategoryResponse;
import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse getCategoryById(Long id);
    List<CategoryResponse> getAllCategories();
    List<CategoryResponse> getAllCategoriesForAdmin();
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);
    void updateCategoryOrder(List<CategoryOrderRequest> orderRequests);
    void hideCategory(Long id);
    void showCategory(Long id);
}
