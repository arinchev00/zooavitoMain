import api from './axios';

export const getSubcategoriesByCategoryId = (categoryId) => 
  api.get(`/subcategories/by-category/${categoryId}`);

export const createSubcategory = (data) => 
  api.post('/subcategories', data);

export const updateSubcategory = (id, data) => 
  api.put(`/subcategories/${id}`, data);

export const deleteSubcategory = (id) => 
  api.delete(`/subcategories/${id}`);