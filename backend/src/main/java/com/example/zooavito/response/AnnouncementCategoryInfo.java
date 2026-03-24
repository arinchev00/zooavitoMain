package com.example.zooavito.response;

import com.example.zooavito.model.Category;
import com.example.zooavito.model.Subcategory;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnnouncementCategoryInfo {
    private CategoryResponse category;
    private SubcategoryResponse subcategory;

    public static AnnouncementCategoryInfo from(Category category, Subcategory subcategory) {
        return AnnouncementCategoryInfo.builder()
                .category(CategoryResponse.from(category))
                .subcategory(SubcategoryResponse.from(subcategory))
                .build();
    }
}
