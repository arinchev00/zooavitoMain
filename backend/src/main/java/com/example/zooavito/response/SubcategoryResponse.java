package com.example.zooavito.response;

import com.example.zooavito.model.Subcategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Ответ с данными подкатегории")
public class SubcategoryResponse {

    @Schema(description = "ID подкатегории", example = "1")
    private Long id;

    @Schema(description = "Название подкатегории", example = "Британские кошки")
    private String title;

    public static SubcategoryResponse from(Subcategory subcategory) {
        return SubcategoryResponse.builder()
                .id(subcategory.getId())
                .title(subcategory.getTitle())
                .build();
    }
}
