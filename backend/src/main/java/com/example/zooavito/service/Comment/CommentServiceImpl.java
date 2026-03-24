package com.example.zooavito.service.Comment;

import com.example.zooavito.model.Announcement;
import com.example.zooavito.model.Comment;
import com.example.zooavito.model.User;
import com.example.zooavito.repository.AnnouncementRepository;
import com.example.zooavito.repository.CommentRepository;
import com.example.zooavito.repository.UserRepository;
import com.example.zooavito.request.CommentRequest;
import com.example.zooavito.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentResponse createComment(Long announcementId, CommentRequest request, String userEmail) {
        log.info("=== СОЗДАНИЕ КОММЕНТАРИЯ ===");

        User author = userRepository.findByEmail(userEmail);
        if (author == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Пользователь не найден");
        }

        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Объявление не найдено с id: " + announcementId
                ));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .author(author)
                .announcement(announcement)
                .build();

        Comment savedComment = commentRepository.save(comment);
        log.info("✅ Комментарий создан с ID: {}", savedComment.getId());

        return CommentResponse.from(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponse getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Комментарий не найден с id: " + id
                ));
        return CommentResponse.from(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByAnnouncementId(Long announcementId) {
        return commentRepository.findByAnnouncementIdOrderByCreatedAtDesc(announcementId)
                .stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByUserId(Long userId) {
        log.info("=== ПОЛУЧЕНИЕ КОММЕНТАРИЕВ ПОЛЬЗОВАТЕЛЯ ID: {} ===", userId);

        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Пользователь не найден с id: " + userId
            );
        }

        List<Comment> comments = commentRepository.findByAuthorId(userId);
        log.info("Найдено комментариев: {}", comments.size());

        return comments.stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByAnnouncementId(Long announcementId, Pageable pageable) {
        return commentRepository.findByAnnouncementId(announcementId, pageable)
                .map(CommentResponse::from);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(Long id, CommentRequest request, String userEmail) {
        log.info("=== ОБНОВЛЕНИЕ КОММЕНТАРИЯ ID: {} ===", id);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Комментарий не найден с id: " + id
                ));

        if (!comment.getAuthor().getEmail().equals(userEmail) && !isAdmin(userEmail)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Нет прав на редактирование этого комментария"
            );
        }

        comment.setContent(request.getContent());
        Comment updatedComment = commentRepository.save(comment);
        log.info("✅ Комментарий обновлен");

        return CommentResponse.from(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id, String userEmail) {
        log.info("=== УДАЛЕНИЕ КОММЕНТАРИЯ ID: {} ===", id);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Комментарий не найден с id: " + id
                ));

        boolean isAuthor = comment.getAuthor().getEmail().equals(userEmail);
        boolean isAnnouncementOwner = comment.getAnnouncement().getUser().getEmail().equals(userEmail);
        boolean isAdmin = isAdmin(userEmail);

        if (!isAuthor && !isAnnouncementOwner && !isAdmin) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Нет прав на удаление этого комментария"
            );
        }

        commentRepository.delete(comment);
        log.info("✅ Комментарий удален");
    }

    private boolean isAdmin(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) return false;
        return user.getRoles().stream()
                .anyMatch(role -> "ROLE_ADMIN".equals(role.getTitle()));
    }
}
