package com.example.zooavito.response;

import com.example.zooavito.model.Announcement;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
public class AnnouncementResponse {
    private Long id;
    private String title;
    private int price;
    private String description;
    private LocalDate dateOfPublication;
    private UserSummaryResponse user;
    private CategoryResponse category;
    private SubcategoryResponse subcategory;
    private Set<ImageResponse> images;
    private List<CommentResponse> comments;
    private long commentsCount;

    public static AnnouncementResponse from(Announcement announcement) {
        CategoryResponse categoryResponse = null;
        if (announcement.getCategories() != null && !announcement.getCategories().isEmpty()) {
            categoryResponse = CategoryResponse.from(announcement.getCategories().iterator().next());
        }

        SubcategoryResponse subcategoryResponse = null;
        if (announcement.getSubcategories() != null && !announcement.getSubcategories().isEmpty()) {
            subcategoryResponse = SubcategoryResponse.from(announcement.getSubcategories().iterator().next());
        }

        Set<ImageResponse> imageResponses = null;
        if (announcement.getImages() != null) {
            imageResponses = announcement.getImages().stream()
                    .map(ImageResponse::from)
                    .collect(Collectors.toSet());
        }

        List<CommentResponse> commentResponses = null;
        long commentsCount = 0;
        if (announcement.getComments() != null) {
            commentsCount = announcement.getComments().size();
            commentResponses = announcement.getComments().stream()
                    .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                    .limit(5)
                    .map(CommentResponse::from)
                    .collect(Collectors.toList());
        }

        return AnnouncementResponse.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .price(announcement.getPrice())
                .description(announcement.getDescription())
                .dateOfPublication(announcement.getDateOfPublication())
                .user(UserSummaryResponse.from(announcement.getUser()))
                .category(categoryResponse)
                .subcategory(subcategoryResponse)
                .images(imageResponses)
                .comments(commentResponses)
                .commentsCount(commentsCount)
                .build();
    }
}