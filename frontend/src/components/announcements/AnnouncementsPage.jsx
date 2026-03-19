import React, { useState, useEffect, useCallback, useMemo } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useCategories } from '../../context/CategoryContext';
import { getAllAnnouncements } from '../../api/announcements';
import AnnouncementCard from './AnnouncementCard';
import AnnouncementFilter from './AnnouncementFilter';

const AnnouncementsPage = () => {
  const { isAuthenticated } = useAuth();
  const { categories } = useCategories();
  const [searchParams, setSearchParams] = useSearchParams();
  const navigate = useNavigate();
  
  const [announcements, setAnnouncements] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [subcategoryNames, setSubcategoryNames] = useState({});
  
  const categoryId = searchParams.get('categoryId');
  const subcategoryId = searchParams.get('subcategoryId');
  const minPrice = searchParams.get('minPrice');
  const maxPrice = searchParams.get('maxPrice');

  const [filters, setFilters] = useState({
    categoryId: categoryId || '',
    subcategoryId: subcategoryId || '',
    minPrice: minPrice || '',
    maxPrice: maxPrice || ''
  });

  // Загружаем название подкатегории если есть subcategoryId
  useEffect(() => {
    if (subcategoryId && !subcategoryNames[subcategoryId]) {
      fetchSubcategoryName(subcategoryId);
    }
  }, [subcategoryId]);

  const fetchSubcategoryName = async (id) => {
    try {
      const response = await fetch(`/api/subcategories/${id}`);
      const data = await response.json();
      setSubcategoryNames(prev => ({
        ...prev,
        [id]: data.title
      }));
    } catch (error) {
      console.error('Ошибка при загрузке подкатегории:', error);
    }
  };

  useEffect(() => {
    setFilters({
      categoryId: categoryId || '',
      subcategoryId: subcategoryId || '',
      minPrice: minPrice || '',
      maxPrice: maxPrice || ''
    });
  }, [categoryId, subcategoryId, minPrice, maxPrice]);

  const fetchAnnouncements = useCallback(async () => {
    try {
      setLoading(true);
      const response = await getAllAnnouncements();
      setAnnouncements(response.data);
    } catch (error) {
      console.error('Ошибка при загрузке объявлений:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchAnnouncements();
  }, [fetchAnnouncements]);

  const handleFilterChange = useCallback((newFilters) => {
    const params = new URLSearchParams();
    if (newFilters.categoryId) params.append('categoryId', newFilters.categoryId);
    if (newFilters.subcategoryId) params.append('subcategoryId', newFilters.subcategoryId);
    if (newFilters.minPrice) params.append('minPrice', newFilters.minPrice);
    if (newFilters.maxPrice) params.append('maxPrice', newFilters.maxPrice);
    
    setSearchParams(params);
  }, [setSearchParams]);

  // Функция для получения названия фильтра
  const getFilterDisplayName = () => {
    if (filters.subcategoryId && filters.categoryId) {
      const category = categories.find(c => c.id === parseInt(filters.categoryId));
      const subcategoryName = subcategoryNames[filters.subcategoryId];
      
      if (category && subcategoryName) {
        return `${category.title} / ${subcategoryName}`;
      } else if (category) {
        return category.title;
      }
    } else if (filters.categoryId) {
      const category = categories.find(c => c.id === parseInt(filters.categoryId));
      if (category) {
        return category.title;
      }
    }
    return null;
  };

  const getPageTitle = () => {
    const filterName = getFilterDisplayName();
    if (filterName) {
      return `Объявления по фильтру: ${filterName}`;
    }
    return 'Все объявления';
  };

  const filteredAnnouncements = useMemo(() => {
    return announcements.filter(ann => {
      const matchesSearch = searchQuery === '' || 
        ann.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        ann.description?.toLowerCase().includes(searchQuery.toLowerCase());

      const matchesCategory = !filters.categoryId || 
        ann.category?.id === parseInt(filters.categoryId);

      const matchesSubcategory = !filters.subcategoryId || 
        ann.subcategory?.id === parseInt(filters.subcategoryId);

      const matchesMinPrice = !filters.minPrice || 
        ann.price >= parseInt(filters.minPrice);
      const matchesMaxPrice = !filters.maxPrice || 
        ann.price <= parseInt(filters.maxPrice);

      return matchesSearch && matchesCategory && matchesSubcategory && 
             matchesMinPrice && matchesMaxPrice;
    });
  }, [announcements, searchQuery, filters]);

  return (
    <div className="container">
      {/* Поисковая строка */}
      <div className="search-container">
        <div className="row align-items-center">
          <div className="col-md-8">
            <div className="input-group">
              <input 
                type="text" 
                className="form-control search-input" 
                placeholder="Что ищете? Например, 'щенки' или 'котята'..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
              <div className="input-group-append">
                <button className="btn search-btn" type="button">
                  <i className="fas fa-search"></i> Поиск
                </button>
              </div>
            </div>
          </div>

          <div className="col-md-4 text-md-right mt-3 mt-md-0">
            {isAuthenticated && (
              <button 
                className="btn btn-add-ad"
                onClick={() => navigate('/announcements/create')}
              >
                <i className="fas fa-plus-circle"></i> Подать объявление
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Компонент фильтра */}
      <AnnouncementFilter 
        initialFilters={filters}
        onFilterChange={handleFilterChange} 
      />

      {/* Заголовок и счетчик */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>{getPageTitle()}</h2>
        <span className="text-muted">Найдено: {filteredAnnouncements.length}</span>
      </div>

      {/* Список объявлений */}
      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-success" role="status">
            <span className="sr-only">Загрузка...</span>
          </div>
        </div>
      ) : filteredAnnouncements.length > 0 ? (
        <div className="row">
          {filteredAnnouncements.map(announcement => (
            <AnnouncementCard key={announcement.id} announcement={announcement} />
          ))}
        </div>
      ) : (
        <div className="text-center py-5">
          <i className="fas fa-box-open" style={{ fontSize: '48px', color: '#ccc' }}></i>
          <h4 className="mt-3">Объявлений не найдено</h4>
          <p className="text-muted">Попробуйте изменить параметры поиска или фильтры</p>
        </div>
      )}
    </div>
  );
};

export default AnnouncementsPage;