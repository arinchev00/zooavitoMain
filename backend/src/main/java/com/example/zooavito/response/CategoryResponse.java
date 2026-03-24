package com.example.zooavito.response;

import com.example.zooavito.model.Category;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponse {
    private Long id;
    private String title;
    private Integer displayOrder;
    private Boolean isHidden;  // Добавляем поле

    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .title(category.getTitle())
                .displayOrder(category.getDisplayOrder())
                .isHidden(category.getIsHidden())
                .build();
    }
}