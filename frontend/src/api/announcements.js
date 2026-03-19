import api from './axios';

export const getAllAnnouncements = (filters = {}) => {
  const params = new URLSearchParams();
  if (filters.categoryId) params.append('categoryId', filters.categoryId);
  if (filters.subcategoryId) params.append('subcategoryId', filters.subcategoryId);
  if (filters.minPrice) params.append('minPrice', filters.minPrice);
  if (filters.maxPrice) params.append('maxPrice', filters.maxPrice);
  
  const url = `/announcement${params.toString() ? `?${params.toString()}` : ''}`;
  return api.get(url);
};

export const getAnnouncementById = (id) => api.get(`/announcement/${id}`);
export const createAnnouncement = (formData) => api.post('/announcement', formData);
export const updateAnnouncement = (id, formData) => api.put(`/announcement/${id}`, formData);
export const deleteAnnouncement = (id) => api.delete(`/announcement/${id}`);

// НОВЫЙ МЕТОД: Получение только своих объявлений
export const getMyAnnouncements = () => {
  return api.get('/announcement/user/me');
};