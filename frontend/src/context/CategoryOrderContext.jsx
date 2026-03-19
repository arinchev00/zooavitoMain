import React, { createContext, useState, useContext, useEffect } from 'react';
import { getAllCategories } from '../api/categories';

const CategoryOrderContext = createContext();

export const useCategoryOrder = () => useContext(CategoryOrderContext);

export const CategoryOrderProvider = ({ children }) => {
  const [categoryOrder, setCategoryOrder] = useState([]);
  const [hiddenCategories, setHiddenCategories] = useState(() => {
    const saved = localStorage.getItem('hiddenCategories');
    return saved ? JSON.parse(saved) : [];
  });

  // Загружаем порядок из БД через категории (они должны приходить отсортированными)
  useEffect(() => {
    const loadOrderFromServer = async () => {
      try {
        const response = await getAllCategories();
        // Сервер уже должен возвращать категории, отсортированные по displayOrder
        // Но на всякий случай сортируем еще раз
        const sortedCategories = [...response.data].sort((a, b) => 
          (a.displayOrder ?? 0) - (b.displayOrder ?? 0)
        );
        const orderFromServer = sortedCategories.map(cat => cat.id);
        setCategoryOrder(orderFromServer);
      } catch (error) {
        console.error('Ошибка при загрузке порядка категорий:', error);
      }
    };

    loadOrderFromServer();
  }, []);

  // Сохраняем скрытые категории в localStorage
  useEffect(() => {
    localStorage.setItem('hiddenCategories', JSON.stringify(hiddenCategories));
  }, [hiddenCategories]);

  // Функция для обновления порядка категорий
  const updateCategoryOrder = (newOrder) => {
    setCategoryOrder(newOrder);
  };

  // Функция для скрытия/показа категории
  const toggleCategoryVisibility = (categoryId) => {
    setHiddenCategories(prev => 
      prev.includes(categoryId)
        ? prev.filter(id => id !== categoryId)
        : [...prev, categoryId]
    );
  };

  // Функция для получения отсортированных и отфильтрованных категорий
  const getVisibleCategories = (allCategories) => {
    if (!allCategories || allCategories.length === 0) return [];
    
    // Сортируем по displayOrder из самих категорий
    let orderedCategories = [...allCategories].sort((a, b) => 
      (a.displayOrder ?? 0) - (b.displayOrder ?? 0)
    );

    // Фильтруем скрытые
    return orderedCategories.filter(cat => !hiddenCategories.includes(cat.id));
  };

  const value = {
    categoryOrder,
    hiddenCategories,
    updateCategoryOrder,
    toggleCategoryVisibility,
    getVisibleCategories
  };

  return (
    <CategoryOrderContext.Provider value={value}>
      {children}
    </CategoryOrderContext.Provider>
  );
};