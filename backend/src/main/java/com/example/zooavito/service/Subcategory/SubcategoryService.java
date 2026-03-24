package com.example.zooavito.service.Subcategory;

import com.example.zooavito.request.SubcategoryRequest;
import com.example.zooavito.response.SubcategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubcategoryService {
    SubcategoryResponse createSubcategory(SubcategoryRequest request);
    SubcategoryResponse getSubcategoryById(Long id);
    List<SubcategoryResponse> getSubcategoriesByCategoryId(Long categoryId);
    Page<SubcategoryResponse> getSubcategoriesByCategoryId(Long categoryId, Pageable pageable);
    List<SubcategoryResponse> getAllSubcategories();
    SubcategoryResponse updateSubcategory(Long id, SubcategoryRequest request);
    void deleteSubcategory(Long id);
}
