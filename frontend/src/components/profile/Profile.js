import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { getCurrentUser, updateUserProfile } from '../../api/user'; // Добавляем импорт
import './Profile.css';

const Profile = () => {
  const navigate = useNavigate();
  const { user, isAuthenticated, loading: authLoading, updateUserData } = useAuth();
  const [isEditing, setIsEditing] = useState(false);
  const [profileData, setProfileData] = useState(null); // Данные с сервера
  const [formData, setFormData] = useState({
    fullName: '',
    phone: '',
    email: ''
  });
  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmNewPassword: ''
  });
  const [loading, setLoading] = useState(false);
  const [loadingProfile, setLoadingProfile] = useState(true); // Загрузка данных профиля
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Загружаем актуальные данные с сервера при загрузке страницы
  useEffect(() => {
    const loadProfileData = async () => {
      if (!isAuthenticated) return;
      
      try {
        setLoadingProfile(true);
        const response = await getCurrentUser();
        console.log('Данные профиля с сервера:', response.data);
        setProfileData(response.data);
        
        // Заполняем форму данными с сервера
        setFormData({
          fullName: response.data.fullName || '',
          phone: response.data.telephoneNumber || '',
          email: response.data.email || ''
        });
      } catch (error) {
        console.error('Ошибка при загрузке профиля:', error);
        setError('Не удалось загрузить данные профиля');
      } finally {
        setLoadingProfile(false);
      }
    };

    loadProfileData();
  }, [isAuthenticated]);

  // Редирект если не авторизован
  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      navigate('/login');
    }
  }, [authLoading, isAuthenticated, navigate]);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handlePasswordChange = (e) => {
    setPasswordData({
      ...passwordData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
  e.preventDefault();
  setLoading(true);
  setError('');
  setSuccess('');

  // Валидация
  if (passwordData.newPassword && passwordData.newPassword.length < 6) {
    setError('Новый пароль должен содержать минимум 6 символов');
    setLoading(false);
    return;
  }

  if (passwordData.newPassword && passwordData.newPassword !== passwordData.confirmNewPassword) {
    setError('Новый пароль и подтверждение не совпадают');
    setLoading(false);
    return;
  }

  try {
    // Формируем данные для отправки - ВСЕГДА отправляем все поля!
    const updateData = {
      fullName: formData.fullName || null,
      telephoneNumber: formData.phone || null,
      newPassword: passwordData.newPassword || null,
      confirmNewPassword: passwordData.confirmNewPassword || null,
      currentPassword: passwordData.currentPassword
    };

    console.log('Отправляемые данные:', updateData); // Для отладки

    // Отправляем запрос на бэкенд
    const response = await updateUserProfile(updateData);
    
    // Обновляем данные в контексте
    updateUserData({
      fullName: response.data.fullName,
      phone: response.data.telephoneNumber
    });
    
    // Обновляем локальные данные профиля
    setProfileData(response.data);
    
    setSuccess('Профиль успешно обновлен');
    setIsEditing(false);
    
    // Очищаем поля паролей
    setPasswordData({
      currentPassword: '',
      newPassword: '',
      confirmNewPassword: ''
    });
  } catch (error) {
    console.error('Ошибка при обновлении профиля:', error);
    if (error.response?.status === 400) {
      setError('Неверный текущий пароль');
    } else {
      setError('Не удалось обновить профиль');
    }
  } finally {
    setLoading(false);
  }
};

  const formatPhone = (phone) => {
    if (!phone) return '';
    const cleaned = phone.replace(/\D/g, '');
    if (cleaned.length === 11) {
      return `+${cleaned[0]} (${cleaned.slice(1, 4)}) ${cleaned.slice(4, 7)}-${cleaned.slice(7, 9)}-${cleaned.slice(9, 11)}`;
    }
    return phone;
  };

  if (authLoading || loadingProfile) {
    return (
      <div className="container text-center py-5">
        <div className="spinner-border text-success" role="status">
          <span className="sr-only">Загрузка...</span>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return null;
  }

  // Используем данные с сервера (profileData) или из контекста (user) как fallback
  const displayData = profileData || user;

  return (
    <div className="container profile-page">
      <div className="profile-header">
        <h1>Личный кабинет</h1>
        {!isEditing && (
          <button 
            className="btn btn-primary"
            onClick={() => setIsEditing(true)}
          >
            <i className="fas fa-edit"></i> Редактировать профиль
          </button>
        )}
      </div>

      {error && <div className="alert alert-danger">{error}</div>}
      {success && <div className="alert alert-success">{success}</div>}

      <div className="profile-card">
        {!isEditing ? (
          // Режим просмотра
          <div className="profile-info">
            <div className="info-row">
              <div className="info-label">
                <i className="fas fa-user"></i>
                <span>Полное имя:</span>
              </div>
              <div className="info-value">{displayData?.fullName || 'Не указано'}</div>
            </div>

            <div className="info-row">
              <div className="info-label">
                <i className="fas fa-envelope"></i>
                <span>Email:</span>
              </div>
              <div className="info-value">{displayData?.email}</div>
            </div>

            <div className="info-row">
              <div className="info-label">
                <i className="fas fa-phone"></i>
                <span>Телефон:</span>
              </div>
              <div className="info-value">{formatPhone(displayData?.telephoneNumber) || 'Не указан'}</div>
            </div>

            <div className="info-row">
              <div className="info-label">
                <i className="fas fa-tag"></i>
                <span>Роль:</span>
              </div>
              <div className="info-value">
                {displayData?.roles?.includes('ROLE_ADMIN') ? 'Администратор' : 'Пользователь'}
              </div>
            </div>
          </div>
        ) : (
          // Режим редактирования
          <form onSubmit={handleSubmit} className="profile-form">
            <div className="form-group">
              <label htmlFor="fullName">
                <i className="fas fa-user"></i> Полное имя
              </label>
              <input
                type="text"
                id="fullName"
                name="fullName"
                value={formData.fullName}
                onChange={handleChange}
                className="form-control"
                placeholder="Введите ваше полное имя"
              />
            </div>

            <div className="form-group">
              <label htmlFor="phone">
                <i className="fas fa-phone"></i> Телефон
              </label>
              <input
                type="tel"
                id="phone"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                className="form-control"
                placeholder="+7 (999) 123-45-67"
              />
            </div>

            <div className="form-group">
              <label htmlFor="email">
                <i className="fas fa-envelope"></i> Email
              </label>
              <input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                className="form-control"
                disabled
              />
              <small className="text-muted">Email нельзя изменить</small>
            </div>

            <div className="password-section">
              <h5>Изменение пароля (необязательно)</h5>
              
              <div className="form-group">
                <label htmlFor="currentPassword">
                  <i className="fas fa-lock"></i> Текущий пароль
                </label>
                <input
                  type="password"
                  id="currentPassword"
                  name="currentPassword"
                  value={passwordData.currentPassword}
                  onChange={handlePasswordChange}
                  className="form-control"
                  placeholder="Введите текущий пароль"
                  required={isEditing}
                />
              </div>

              <div className="form-group">
                <label htmlFor="newPassword">
                  <i className="fas fa-key"></i> Новый пароль
                </label>
                <input
                  type="password"
                  id="newPassword"
                  name="newPassword"
                  value={passwordData.newPassword}
                  onChange={handlePasswordChange}
                  className="form-control"
                  placeholder="Оставьте пустым, если не хотите менять"
                  minLength="6"
                />
              </div>

              <div className="form-group">
                <label htmlFor="confirmNewPassword">
                  <i className="fas fa-key"></i> Подтвердите новый пароль
                </label>
                <input
                  type="password"
                  id="confirmNewPassword"
                  name="confirmNewPassword"
                  value={passwordData.confirmNewPassword}
                  onChange={handlePasswordChange}
                  className="form-control"
                  placeholder="Подтвердите новый пароль"
                />
              </div>
            </div>

            <div className="form-actions">
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => {
                  setIsEditing(false);
                  setFormData({
                    fullName: profileData?.fullName || '',
                    phone: profileData?.telephoneNumber || '',
                    email: profileData?.email || ''
                  });
                  setPasswordData({
                    currentPassword: '',
                    newPassword: '',
                    confirmNewPassword: ''
                  });
                  setError('');
                }}
                disabled={loading}
              >
                Отмена
              </button>
              <button
                type="submit"
                className="btn btn-success"
                disabled={loading || !passwordData.currentPassword}
              >
                {loading ? (
                  <>
                    <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                    Сохранение...
                  </>
                ) : (
                  'Сохранить изменения'
                )}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};

export default Profile;