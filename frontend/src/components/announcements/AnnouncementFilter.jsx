import React, { useState, useEffect } from 'react';
import { useCategories } from '../../context/CategoryContext';
import { useCategoryOrder } from '../../context/CategoryOrderContext'; // Добавляем импорт

const AnnouncementFilter = ({ initialFilters = {}, onFilterChange }) => {
  const { categories } = useCategories();
  const { getVisibleCategories } = useCategoryOrder(); // Получаем функцию для сортировки
  
  const [subcategories, setSubcategories] = useState([]);
  const [sortedCategories, setSortedCategories] = useState([]); // Новое состояние
  const [filters, setFilters] = useState({
    categoryId: initialFilters.categoryId || '',
    subcategoryId: initialFilters.subcategoryId || '',
    minPrice: initialFilters.minPrice || '',
    maxPrice: initialFilters.maxPrice || ''
  });

  // Сортируем категории согласно порядку из контекста
  useEffect(() => {
    if (categories && categories.length > 0) {
      const visible = getVisibleCategories(categories);
      setSortedCategories(visible);
    }
  }, [categories, getVisibleCategories]);

  useEffect(() => {
    if (filters.categoryId) {
      loadSubcategories(filters.categoryId);
    } else {
      setSubcategories([]);
    }
  }, [filters.categoryId]);

  useEffect(() => {
    setFilters({
      categoryId: initialFilters.categoryId || '',
      subcategoryId: initialFilters.subcategoryId || '',
      minPrice: initialFilters.minPrice || '',
      maxPrice: initialFilters.maxPrice || ''
    });
  }, [initialFilters]);

const loadSubcategories = async (categoryId) => {
  try {
    // Было (прямой вызов бэкенда)
    // const response = await fetch(`http://localhost:8081/v1/api/subcategories/by-category/${categoryId}`);
    
    // Стало (через Nginx)
    const response = await fetch(`/api/subcategories/by-category/${categoryId}`);
    const data = await response.json();
    setSubcategories(data);
  } catch (error) {
    console.error('Ошибка при загрузке подкатегорий:', error);
  }
};

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    const newFilters = { ...filters, [name]: value };
    
    if (name === 'categoryId') {
      newFilters.subcategoryId = '';
    }
    
    setFilters(newFilters);
    onFilterChange(newFilters);
  };

  const handleReset = () => {
    const resetFilters = {
      categoryId: '',
      subcategoryId: '',
      minPrice: '',
      maxPrice: ''
    };
    setFilters(resetFilters);
    setSubcategories([]);
    onFilterChange(resetFilters);
  };

  return (
    <div className="card mb-4">
      <div className="card-body">
        <h5 className="card-title mb-3">Фильтр объявлений</h5>
        
        <div className="row">
          <div className="col-md-3 mb-3">
            <label className="form-label">Категория</label>
            <select 
              className="form-control" 
              name="categoryId" 
              value={filters.categoryId}
              onChange={handleFilterChange}
            >
              <option value="">Все категории</option>
              {sortedCategories.map(cat => ( // Используем sortedCategories вместо categories
                <option key={cat.id} value={cat.id}>{cat.title}</option>
              ))}
            </select>
          </div>

          <div className="col-md-3 mb-3">
            <label className="form-label">Подкатегория</label>
            <select 
              className="form-control" 
              name="subcategoryId" 
              value={filters.subcategoryId}
              onChange={handleFilterChange}
              disabled={!filters.categoryId}
            >
              <option value="">Все подкатегории</option>
              {subcategories.map(sub => (
                <option key={sub.id} value={sub.id}>{sub.title}</option>
              ))}
            </select>
          </div>

          <div className="col-md-2 mb-3">
            <label className="form-label">Цена от</label>
            <input
              type="number"
              className="form-control"
              name="minPrice"
              value={filters.minPrice}
              onChange={handleFilterChange}
              placeholder="0"
              min="0"
            />
          </div>

          <div className="col-md-2 mb-3">
            <label className="form-label">Цена до</label>
            <input
              type="number"
              className="form-control"
              name="maxPrice"
              value={filters.maxPrice}
              onChange={handleFilterChange}
              placeholder="100000"
              min="0"
            />
          </div>

          <div className="col-md-2 mb-3 d-flex align-items-end">
            <button 
              className="btn btn-outline-secondary w-100" 
              onClick={handleReset}
            >
              <i className="fas fa-undo mr-2"></i> Сбросить
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AnnouncementFilter;