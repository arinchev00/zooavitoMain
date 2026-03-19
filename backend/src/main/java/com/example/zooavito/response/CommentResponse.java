package com.example.zooavito.response;

import com.example.zooavito.model.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
@Builder
@Schema(description = "Ответ с данными комментария")
public class CommentResponse {

    @Schema(description = "ID комментария", example = "1")
    private Long id;

    @Schema(description = "Текст комментария", example = "Отличное объявление!")
    private String content;

    @Schema(description = "Автор комментария")
    private UserSummaryResponse author;

    @Schema(description = "ID объявления", example = "1")
    private Long announcementId;

    @Schema(description = "Дата создания", example = "2026-03-09 10:30:45")
    private String createdAt;

    @Schema(description = "Дата обновления", example = "2026-03-09 10:30:45")
    private String updatedAt;

    public static CommentResponse from(Comment comment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(UserSummaryResponse.from(comment.getAuthor()))
                .announcementId(comment.getAnnouncement().getId())
                .createdAt(comment.getCreatedAt() != null ? comment.getCreatedAt().format(formatter) : null)
                .updatedAt(comment.getUpdatedAt() != null ? comment.getUpdatedAt().format(formatter) : null)
                .build();
    }
}
