package com.example.zooavito.service.Comment;

import com.example.zooavito.request.CommentRequest;
import com.example.zooavito.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    CommentResponse createComment(Long announcementId, CommentRequest request, String userEmail);
    CommentResponse getCommentById(Long id);
    List<CommentResponse> getCommentsByAnnouncementId(Long announcementId);
    Page<CommentResponse> getCommentsByAnnouncementId(Long announcementId, Pageable pageable);
    List<CommentResponse> getCommentsByUserId(Long userId);  // ← Добавляем метод
    CommentResponse updateComment(Long id, CommentRequest request, String userEmail);
    void deleteComment(Long id, String userEmail);
}