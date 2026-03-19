import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { getMyAnnouncements } from '../../api/announcements';
import AnnouncementCard from './AnnouncementCard';
import './MyAnnouncements.css';

const MyAnnouncements = () => {
  const navigate = useNavigate();
  const { user, isAuthenticated, loading: authLoading } = useAuth(); // Добавляем authLoading из контекста
  const [announcements, setAnnouncements] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Ждем завершения загрузки аутентификации
  useEffect(() => {
    if (!authLoading) {
      if (!isAuthenticated) {
        navigate('/login');
      } else {
        loadMyAnnouncements();
      }
    }
  }, [authLoading, isAuthenticated, navigate]);

  const loadMyAnnouncements = async () => {
    try {
      setLoading(true);
      const response = await getMyAnnouncements();
      console.log('Мои объявления:', response.data);
      setAnnouncements(response.data);
    } catch (error) {
      console.error('Ошибка при загрузке объявлений:', error);
      setError('Не удалось загрузить ваши объявления');
    } finally {
      setLoading(false);
    }
  };

  // Показываем загрузку, пока проверяется аутентификация
  if (authLoading || loading) {
    return (
      <div className="container text-center py-5">
        <div className="spinner-border text-success" role="status">
          <span className="sr-only">Загрузка...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="container my-announcements-page">
      {/* Контейнер с кнопками */}
      <div className="search-container">
        <div className="row align-items-center">
          <div className="col-md-6">
            <button 
              className="btn btn-all-ads"
              onClick={() => navigate('/announcements')}
            >
              <i className="fas fa-arrow-left"></i> Все объявления
            </button>
          </div>

          <div className="col-md-6 text-md-right mt-3 mt-md-0">
            <button 
              className="btn btn-add-ad"
              onClick={() => navigate('/announcements/create')}
            >
              <i className="fas fa-plus-circle"></i> Подать объявление
            </button>
          </div>
        </div>
      </div>

      <div className="page-header">
        <h1>Мои объявления</h1>
      </div>

      {error && (
        <div className="alert alert-danger" role="alert">
          {error}
        </div>
      )}

      {announcements.length === 0 ? (
        <div className="empty-state">
          <i className="fas fa-box-open"></i>
          <h3>У вас пока нет объявлений</h3>
          <p>Создайте свое первое объявление и начните продавать!</p>
          <button 
            className="btn btn-primary btn-lg"
            onClick={() => navigate('/announcements/create')}
          >
            Создать объявление
          </button>
        </div>
      ) : (
        <>
          <div className="announcements-stats">
            <p className="text-muted">
              Всего объявлений: <strong>{announcements.length}</strong>
            </p>
          </div>

          <div className="row">
            {announcements.map(announcement => (
              <div key={announcement.id} className="col-lg-3 col-md-4 col-sm-6 mb-4">
                <AnnouncementCard announcement={announcement} />
              </div>
            ))}
          </div>
        </>
      )}
    </div>
  );
};

export default MyAnnouncements;