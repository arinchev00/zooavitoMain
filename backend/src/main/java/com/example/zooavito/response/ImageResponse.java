package com.example.zooavito.response;

import com.example.zooavito.model.Image;
import lombok.Builder;
import lombok.Data;

import java.util.Base64;

@Data
@Builder
public class ImageResponse {
    private Long id;
    private String name;
    private String originalFileName;
    private Long size;
    private String contentType;
    private String base64Image;  // Изображение в base64 для отображения
    private boolean isMain;

    public static ImageResponse from(Image image) {
        String base64 = null;
        if (image.getBytes() != null && image.getBytes().length > 0) {
            base64 = Base64.getEncoder().encodeToString(image.getBytes());
        }

        return ImageResponse.builder()
                .id(image.getId())
                .name(image.getName())
                .originalFileName(image.getOriginalFileName())
                .size(image.getSize())
                .contentType(image.getContentType())
                .base64Image(base64)
                .isMain(image.isMain())  // добавить!
                .build();
    }
}
