import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { getAnnouncementById, deleteAnnouncement } from '../../api/announcements';
import { createComment, getCommentsByAnnouncement, updateComment, deleteComment } from '../../api/comments';
import './AnnouncementDetail.css';

const AnnouncementDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  
  const [announcement, setAnnouncement] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [editingCommentId, setEditingCommentId] = useState(null);
  const [editingContent, setEditingContent] = useState('');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [selectedImage, setSelectedImage] = useState(null);

  // Отладка
  useEffect(() => {
    console.log('Current user:', user);
    console.log('User email:', user?.email);
    console.log('Is admin?', user?.role === 'ROLE_ADMIN');
  }, [user]);

  useEffect(() => {
    loadAnnouncement();
    loadComments();
  }, [id]);

  const loadAnnouncement = async () => {
    try {
      setLoading(true);
      const response = await getAnnouncementById(id);
      setAnnouncement(response.data);
      
      if (response.data.images?.length > 0) {
        const mainImage = response.data.images.find(img => img.main === true) || response.data.images[0];
        setSelectedImage(mainImage);
      }
    } catch (error) {
      console.error('Ошибка при загрузке объявления:', error);
      setError('Не удалось загрузить объявление');
    } finally {
      setLoading(false);
    }
  };

  const loadComments = async () => {
    try {
      const response = await getCommentsByAnnouncement(id);
      setComments(response.data);
    } catch (error) {
      console.error('Ошибка при загрузке комментариев:', error);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('Вы уверены, что хотите удалить это объявление?')) {
      return;
    }

    try {
      await deleteAnnouncement(id);
      navigate('/announcements');
    } catch (error) {
      console.error('Ошибка при удалении объявления:', error);
      alert('Не удалось удалить объявление');
    }
  };

  const handleCommentSubmit = async (e) => {
    e.preventDefault();
    if (!newComment.trim()) return;

    setSubmitting(true);
    try {
      await createComment(id, { content: newComment });
      setNewComment('');
      loadComments();
    } catch (error) {
      console.error('Ошибка при добавлении комментария:', error);
      alert('Не удалось добавить комментарий');
    } finally {
      setSubmitting(false);
    }
  };

  const handleEditClick = (comment) => {
    setEditingCommentId(comment.id);
    setEditingContent(comment.content);
  };

  const handleEditCancel = () => {
    setEditingCommentId(null);
    setEditingContent('');
  };

  const handleEditSubmit = async (commentId) => {
    if (!editingContent.trim()) return;

    setSubmitting(true);
    try {
      await updateComment(commentId, { content: editingContent });
      setEditingCommentId(null);
      setEditingContent('');
      loadComments();
    } catch (error) {
      console.error('Ошибка при обновлении комментария:', error);
      alert('Не удалось обновить комментарий');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteComment = async (commentId) => {
    if (!window.confirm('Вы уверены, что хотите удалить этот комментарий?')) {
      return;
    }

    try {
      await deleteComment(commentId);
      loadComments();
    } catch (error) {
      console.error('Ошибка при удалении комментария:', error);
      alert('Не удалось удалить комментарий');
    }
  };

  // Проверка на админа (по роли, а не по email)
  const isAdmin = user?.role === 'ROLE_ADMIN';

  // Проверка прав на редактирование комментария (админ или автор)
  const canEditComment = (comment) => {
    if (!isAuthenticated) return false;
    
    // Админ может редактировать любые комментарии
    if (isAdmin) return true;
    
    // Автор может редактировать свой комментарий
    return comment.author?.email === user?.email;
  };

  // Проверка прав на удаление комментария (админ или автор)
  const canDeleteComment = (comment) => {
    if (!isAuthenticated) return false;
    
    // Админ может удалять любые комментарии
    if (isAdmin) return true;
    
    // Автор может удалять свой комментарий
    return comment.author?.email === user?.email;
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString('ru-RU', {
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    });
  };

  const formatPhoneNumber = (phone) => {
    if (!phone) return null;
    const cleaned = phone.replace(/\D/g, '');
    if (cleaned.length === 11) {
      return `+${cleaned[0]} (${cleaned.substring(1, 4)}) ${cleaned.substring(4, 7)}-${cleaned.substring(7, 9)}-${cleaned.substring(9, 11)}`;
    }
    return phone;
  };

  if (loading) {
    return (
      <div className="container text-center py-5">
        <div className="spinner-border text-success" role="status">
          <span className="sr-only">Загрузка...</span>
        </div>
      </div>
    );
  }

  if (error || !announcement) {
    return (
      <div className="container text-center py-5">
        <div className="alert alert-danger">
          {error || 'Объявление не найдено'}
        </div>
        <Link to="/announcements" className="btn btn-primary">
          Вернуться к списку
        </Link>
      </div>
    );
  }

  const isOwner = user?.email === announcement.user?.email;
  const canEdit = isOwner || isAdmin;

  return (
    <div className="container">
      <div className="announcement-detail">
        {/* Галерея изображений */}
        <div className="image-gallery">
          {announcement.images?.length > 0 ? (
            <>
              <div className="main-image">
                <img 
                  src={selectedImage ? `data:${selectedImage.contentType};base64,${selectedImage.base64Image}` : ''} 
                  alt={announcement.title}
                />
              </div>
              {announcement.images.length > 1 && (
                <div className="image-thumbnails">
                  {announcement.images.map((img, index) => (
                    <div 
                      key={img.id}
                      className={`thumbnail ${selectedImage?.id === img.id ? 'active' : ''}`}
                      onClick={() => setSelectedImage(img)}
                    >
                      <img 
                        src={`data:${img.contentType};base64,${img.base64Image}`} 
                        alt={`Фото ${index + 1}`}
                      />
                    </div>
                  ))}
                </div>
              )}
            </>
          ) : (
            <div className="no-image">
              <i className="fas fa-image"></i>
              <p>Нет фотографий</p>
            </div>
          )}
        </div>

        {/* Информация об объявлении */}
        <div className="announcement-info">
          <h1 className="announcement-title">{announcement.title}</h1>
          
          <div className="announcement-price">
            {announcement.price.toLocaleString()} ₽
          </div>

          <div className="announcement-meta">
            <div className="meta-item">
              <i className="fas fa-user"></i>
              <span>{announcement.user?.fullName || announcement.user?.email}</span>
            </div>
            
            {/* Контактные данные (только для авторизованных) */}
            {isAuthenticated ? (
              <>
                {announcement.user?.email && (
                  <div className="meta-item">
                    <i className="fas fa-envelope"></i>
                    <span>{announcement.user.email}</span>
                  </div>
                )}
                {announcement.user?.telephoneNumber && (
                  <div className="meta-item">
                    <i className="fas fa-phone"></i>
                    <span>{formatPhoneNumber(announcement.user.telephoneNumber)}</span>
                  </div>
                )}
              </>
            ) : (
              <div className="meta-item" style={{ 
                backgroundColor: '#f8f9fa', 
                padding: '8px 12px', 
                borderRadius: '4px',
                marginTop: '5px'
              }}>
                <i className="fas fa-lock" style={{ marginRight: '5px', color: '#6c757d' }}></i>
                <span>
                  <Link to="/login" style={{ color: '#28a745', textDecoration: 'none' }}>
                    Войдите
                  </Link>
                  <span style={{ color: '#6c757d' }}>, чтобы увидеть контактные данные</span>
                </span>
              </div>
            )}

            <div className="meta-item">
              <i className="fas fa-calendar"></i>
              <span>{formatDate(announcement.dateOfPublication)}</span>
            </div>
            <div className="meta-item">
              <i className="fas fa-tag"></i>
              <span>
                {announcement.category?.title}
                {announcement.subcategory && ` / ${announcement.subcategory.title}`}
              </span>
            </div>
          </div>

          {announcement.description && (
            <div className="announcement-section">
              <h3>Описание</h3>
              <p className="announcement-description">{announcement.description}</p>
            </div>
          )}

          {announcement.comment && (
            <div className="announcement-section">
              <h3>Комментарий продавца</h3>
              <p className="announcement-comment">{announcement.comment}</p>
            </div>
          )}

          {canEdit && (
            <div className="announcement-actions">
              <Link 
                to={`/announcements/edit/${id}`} 
                className="btn btn-warning"
              >
                <i className="fas fa-edit"></i> Редактировать
              </Link>
              <button 
                onClick={handleDelete} 
                className="btn btn-danger"
              >
                <i className="fas fa-trash"></i> Удалить
              </button>
            </div>
          )}
        </div>
      </div>

      {/* Блок комментариев */}
      <div className="comments-section">
        <h3 className="comments-title">
          <i className="fas fa-comments"></i> Комментарии ({comments.length})
        </h3>

        {/* Форма добавления комментария */}
        {isAuthenticated ? (
          <form onSubmit={handleCommentSubmit} className="comment-form">
            <textarea
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              placeholder="Напишите комментарий..."
              rows="3"
              className="form-control"
              required
            />
            <button 
              type="submit" 
              className="btn btn-primary"
              disabled={submitting || !newComment.trim()}
            >
              {submitting ? 'Отправка...' : 'Отправить комментарий'}
            </button>
          </form>
        ) : (
          <div className="alert alert-info">
            <Link to="/login">Войдите</Link>, чтобы оставить комментарий
          </div>
        )}

        {/* Список комментариев */}
        <div className="comments-list">
          {comments.length > 0 ? (
            comments.map(comment => (
              <div key={comment.id} className="comment-item">
                {editingCommentId === comment.id ? (
                  // Режим редактирования
                  <div className="comment-edit-form">
                    <textarea
                      value={editingContent}
                      onChange={(e) => setEditingContent(e.target.value)}
                      rows="3"
                      className="form-control"
                      autoFocus
                    />
                    <div className="comment-edit-actions">
                      <button
                        onClick={() => handleEditSubmit(comment.id)}
                        className="btn btn-sm btn-success"
                        disabled={submitting}
                      >
                        Сохранить
                      </button>
                      <button
                        onClick={handleEditCancel}
                        className="btn btn-sm btn-secondary"
                      >
                        Отмена
                      </button>
                    </div>
                  </div>
                ) : (
                  // Режим просмотра
                  <>
                    <div className="comment-header">
                      <span className="comment-author">
                        <i className="fas fa-user-circle"></i>
                        {comment.author?.fullName || comment.author?.email}
                      </span>
                      <span className="comment-date">
                        {formatDate(comment.createdAt)}
                      </span>
                      
                      {/* Кнопки действий (только админ или автор) */}
                      {(canEditComment(comment) || canDeleteComment(comment)) && (
                        <div className="comment-actions">
                          {canEditComment(comment) && (
                            <button
                              onClick={() => handleEditClick(comment)}
                              className="btn btn-sm btn-warning"
                              title="Редактировать"
                            >
                              <i className="fas fa-edit"></i>
                            </button>
                          )}
                          
                          {canDeleteComment(comment) && (
                            <button
                              onClick={() => handleDeleteComment(comment.id)}
                              className="btn btn-sm btn-danger"
                              title="Удалить"
                            >
                              <i className="fas fa-trash"></i>
                            </button>
                          )}
                        </div>
                      )}
                    </div>
                    <div className="comment-content">
                      {comment.content}
                    </div>
                  </>
                )}
              </div>
            ))
          ) : (
            <p className="text-muted text-center py-4">
              Пока нет комментариев. Будьте первым!
            </p>
          )}
        </div>
      </div>
    </div>
  );
};

export default AnnouncementDetail;