import api from './axios';

// ========== КАТЕГОРИИ ==========
export const getAllCategories = () => api.get('/categories');
export const getCategoryById = (id) => api.get(`/categories/${id}`);
// Скрыть/показать категорию
export const hideCategory = (id) => api.put(`/categories/${id}/hide`);
export const showCategory = (id) => api.put(`/categories/${id}/show`);

// Админ-панель: управление категориями
export const createCategory = (data) => api.post('/categories', data);
export const updateCategory = (id, data) => api.put(`/categories/${id}`, data);
export const deleteCategory = (id) => api.delete(`/categories/${id}`);

// Управление порядком категорий
export const updateCategoryOrder = (orderData) => {
  return api.put('/categories/order', orderData);
};

// ========== ПОДКАТЕГОРИИ ==========
export const getAllSubcategories = () => api.get('/subcategories');
export const getSubcategoriesByCategoryId = (categoryId) => 
  api.get(`/subcategories/by-category/${categoryId}`);

// Админ-панель: управление подкатегориями
export const createSubcategory = (data) => api.post('/subcategories', data);
export const updateSubcategory = (id, data) => api.put(`/subcategories/${id}`, data);
export const deleteSubcategory = (id) => api.delete(`/subcategories/${id}`);
// Для админ-панели (получить все категории, включая скрытые)
export const getAllCategoriesForAdmin = () => api.get('/categories/admin');