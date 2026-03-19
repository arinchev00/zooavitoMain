import api from './axios';

// Получить информацию о текущем пользователе
export const getCurrentUser = () => {
  return api.get('/user/me');
};

// Обновить профиль пользователя
export const updateUserProfile = (userData) => {
  return api.put('/user/me', userData);
};