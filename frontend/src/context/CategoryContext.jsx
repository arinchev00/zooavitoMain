import React, { createContext, useState, useContext, useEffect } from 'react';
import { getAllCategories } from '../api/categories';

const CategoryContext = createContext();

export const useCategories = () => useContext(CategoryContext);

export const CategoryProvider = ({ children }) => {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadCategories();
  }, []);

  const loadCategories = async () => {
    try {
      setLoading(true);
      const response = await getAllCategories();
      setCategories(response.data);
    } catch (err) {
      setError(err);
      console.error('Ошибка при загрузке категорий:', err);
    } finally {
      setLoading(false);
    }
  };

  // Функция для принудительного обновления категорий
  const refreshCategories = async () => {
    try {
      setLoading(true);
      const response = await getAllCategories();
      setCategories(response.data);
      return response.data;
    } catch (error) {
      console.error('Ошибка при обновлении категорий:', error);
      setError(error);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const value = {
    categories,
    loading,
    error,
    refreshCategories,  // теперь функция определена внутри компонента
    loadCategories      // можно также добавить, если нужно
  };

  return (
    <CategoryContext.Provider value={value}>
      {children}
    </CategoryContext.Provider>
  );
};