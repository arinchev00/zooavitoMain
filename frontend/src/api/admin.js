import api from './axios';

// Получить всех пользователей
export const getAllUsers = () => {
  return api.get('/admin/users');
};

// Заблокировать пользователя
export const blockUser = (userId) => {
  return api.put(`/admin/users/${userId}/block`);
};

// Разблокировать пользователя
export const unblockUser = (userId) => {
  return api.put(`/admin/users/${userId}/unblock`);
};

// Изменить роль пользователя
export const changeUserRole = (userId, role) => {
  return api.put(`/admin/users/${userId}/role?role=${role}`);
};