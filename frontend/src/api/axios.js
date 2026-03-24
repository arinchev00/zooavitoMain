import axios from 'axios';

const instance = axios.create({
  baseURL: '/api',
  timeout: 120000, // Увеличиваем до 2 минут для загрузки фото
});

// Добавляем токен к каждому запросу
instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    console.log('Making request to:', config.url);
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Обрабатываем ответы
instance.interceptors.response.use(
  (response) => {
    console.log('Response received:', response.status);
    return response;
  },
  (error) => {
    console.error('API Error:', error.response?.status, error.message);

    // Специальная обработка для 413 (Request Entity Too Large)
    if (error.response?.status === 413) {
      console.error('File too large error');
      error.userMessage = 'Файлы слишком большие. Уменьшите размер изображений или загрузите меньше файлов.';
    }
    // Если пользователь заблокирован (403)
    else if (error.response?.status === 403) {
      console.log('User is blocked, logging out...');
      localStorage.removeItem('token');
      localStorage.removeItem('userData');
      window.location.href = '/login?blocked=true';
    }
    // Если не авторизован (401)
    else if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('userData');
      window.location.href = '/login';
    }
    
    return Promise.reject(error);
  }
);

export default instance;