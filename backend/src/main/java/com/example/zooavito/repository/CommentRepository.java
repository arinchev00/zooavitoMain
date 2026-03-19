package com.example.zooavito.repository;

import com.example.zooavito.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Найти все комментарии к объявлению
    List<Comment> findByAnnouncementIdOrderByCreatedAtDesc(Long announcementId);

    // С пагинацией
    Page<Comment> findByAnnouncementId(Long announcementId, Pageable pageable);

    // Найти комментарии пользователя
    List<Comment> findByAuthorId(Long userId);
}
