import api from './axios';

export const register = (userData) => api.post('/registration', userData);
export const login = (credentials) => api.post('/auth', credentials);