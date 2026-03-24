package com.example.zooavito.service.Announcement;

import com.example.zooavito.request.AnnouncementRequest;
import com.example.zooavito.response.AnnouncementResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AnnouncementService {
    List<AnnouncementResponse> getAllAnnouncements(Long categoryId, Long subcategoryId, Integer minPrice, Integer maxPrice);
    List<AnnouncementResponse> getAnnouncementsByUserEmail(String userEmail);
    AnnouncementResponse createAnnouncement(AnnouncementRequest request, @Nullable List<MultipartFile> images, String userEmail) throws IOException;
    AnnouncementResponse getAnnouncementById(Long id);
    AnnouncementResponse updateAnnouncement(Long id, AnnouncementRequest request,
                                            @Nullable List<MultipartFile> images,
                                            @Nullable List<Long> imagesToDelete,
                                            @Nullable Long mainImageId,  // добавить!
                                            String userEmail) throws IOException;
    void deleteAnnouncement(Long id, String userEmail);
}