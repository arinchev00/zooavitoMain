import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useCategories } from '../../context/CategoryContext';
import { updateCategoryOrder, hideCategory, showCategory, getAllCategoriesForAdmin } from '../../api/categories';
import {
  DndContext,
  closestCenter,
  KeyboardSensor,
  PointerSensor,
  useSensor,
  useSensors,
} from '@dnd-kit/core';
import {
  arrayMove,
  SortableContext,
  sortableKeyboardCoordinates,
  verticalListSortingStrategy,
} from '@dnd-kit/sortable';
import {
  restrictToVerticalAxis,
  restrictToParentElement,
} from '@dnd-kit/modifiers';
import SortableItem from './SortableItem';
import './CategoryOrder.css';

const CategoryOrder = () => {
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  const { refreshCategories } = useCategories();
  
  const [orderedCategories, setOrderedCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [activeId, setActiveId] = useState(null);

  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: {
        distance: 8,
      },
    }),
    useSensor(KeyboardSensor, {
      coordinateGetter: sortableKeyboardCoordinates,
    })
  );

  // Проверка прав доступа
  useEffect(() => {
    if (!isAuthenticated || user?.role !== 'ROLE_ADMIN') {
      navigate('/announcements');
    }
  }, [isAuthenticated, user, navigate]);

  // Загружаем категории для админ-панели (включая скрытые)
  useEffect(() => {
    loadCategories();
  }, []);

  const loadCategories = async () => {
    try {
      setLoading(true);
      const response = await getAllCategoriesForAdmin();
      // Сортируем по displayOrder на клиенте для уверенности
      const sorted = [...response.data].sort((a, b) => 
        (a.displayOrder ?? 0) - (b.displayOrder ?? 0)
      );
      setOrderedCategories(sorted);
    } catch (error) {
      console.error('Ошибка при загрузке категорий:', error);
      setError('Не удалось загрузить категории');
    } finally {
      setLoading(false);
    }
  };

  const handleDragStart = (event) => {
    setActiveId(event.active.id);
  };

  const handleDragEnd = (event) => {
    const { active, over } = event;
    setActiveId(null);

    if (!over) return;

    if (active.id !== over.id) {
      setOrderedCategories((items) => {
        const oldIndex = items.findIndex((item) => item.id === active.id);
        const newIndex = items.findIndex((item) => item.id === over.id);
        return arrayMove(items, oldIndex, newIndex);
      });
    }
  };

  const handleSaveOrder = async () => {
    try {
      setSaving(true);
      setError('');
      
      // Подготавливаем данные для отправки на сервер
      const orderData = orderedCategories.map((category, index) => ({
        id: category.id,
        order: index
      }));

      console.log('Saving order:', orderData);
      
      // Отправляем на сервер
      await updateCategoryOrder(orderData);
      
      // Обновляем категории в контексте
      if (refreshCategories) {
        await refreshCategories();
      }
      
      setSuccess('Порядок категорий успешно сохранен');
      setTimeout(() => setSuccess(''), 3000);
    } catch (error) {
      console.error('Ошибка при сохранении порядка:', error);
      setError('Не удалось сохранить порядок категорий');
    } finally {
      setSaving(false);
    }
  };

  const handleToggleVisibility = async (categoryId) => {
    try {
      const category = orderedCategories.find(c => c.id === categoryId);
      if (category.isHidden) {
        await showCategory(categoryId);
      } else {
        await hideCategory(categoryId);
      }
      
      // Обновляем список категорий
      await loadCategories();
      
      // Обновляем категории в контексте
      if (refreshCategories) {
        await refreshCategories();
      }
      
      setSuccess(`Категория ${category.isHidden ? 'показана' : 'скрыта'}`);
      setTimeout(() => setSuccess(''), 3000);
    } catch (error) {
      console.error('Ошибка при изменении видимости:', error);
      setError('Не удалось изменить видимость категории');
    }
  };

  const handleReset = () => {
    // Сброс к порядку по умолчанию (по id)
    const defaultOrder = [...orderedCategories].sort((a, b) => a.id - b.id);
    setOrderedCategories(defaultOrder);
  };

  if (!isAuthenticated || user?.role !== 'ROLE_ADMIN') {
    return null;
  }

  if (loading) {
    return (
      <div className="category-order-page">
        <div className="container">
          <div className="text-center py-5">
            <div className="spinner-border text-success" role="status">
              <span className="sr-only">Загрузка...</span>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="category-order-page">
      <div className="container">
        <div className="page-header">
          <h1>Порядок отображения категорий в шапке</h1>
          <button 
            onClick={() => navigate('/admin')} 
            className="btn btn-secondary"
          >
            ← Назад к панели админа
          </button>
        </div>

        {error && <div className="alert alert-danger">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}

        <div className="order-instructions">
          <p>
            <i className="fas fa-info-circle"></i>
            Перетаскивайте категории мышкой, чтобы изменить их порядок отображения в шапке сайта.
            Категории, расположенные выше, будут отображаться левее.
          </p>
        </div>

        <div className="order-container">
          <DndContext
            sensors={sensors}
            collisionDetection={closestCenter}
            onDragStart={handleDragStart}
            onDragEnd={handleDragEnd}
            modifiers={[restrictToVerticalAxis, restrictToParentElement]}
          >
            <SortableContext
              items={orderedCategories.map(c => c.id)}
              strategy={verticalListSortingStrategy}
            >
              <div className="categories-list">
                {orderedCategories.map((category, index) => (
                  <SortableItem 
                    key={category.id} 
                    id={category.id} 
                    category={category}
                    index={index}
                    isHidden={category.isHidden}
                    onToggleVisibility={handleToggleVisibility}
                  />
                ))}
              </div>
            </SortableContext>
          </DndContext>
        </div>

        <div className="order-actions">
          <button
            onClick={handleSaveOrder}
            className="btn btn-success"
            disabled={saving}
          >
            {saving ? (
              <>
                <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                Сохранение...
              </>
            ) : (
              'Сохранить порядок'
            )}
          </button>
          <button
            onClick={handleReset}
            className="btn btn-secondary"
            disabled={saving}
          >
            Сбросить
          </button>
        </div>

        <div className="preview-note">
          <h3>Как это будет выглядеть в шапке:</h3>
          <div className="preview-header">
            {orderedCategories
              .filter(cat => !cat.isHidden)
              .slice(0, 5)
              .map((cat, index, filtered) => (
                <span key={cat.id} className="preview-item">
                  {cat.title}
                  {index < filtered.length - 1 && ' | '}
                </span>
              ))}
            {orderedCategories.filter(cat => !cat.isHidden).length > 5 && (
              <span className="preview-more">...</span>
            )}
          </div>
          <p className="text-muted small">
            * В шапке отображаются первые 5 видимых категорий. Остальные доступны в выпадающем меню "Еще"
          </p>
        </div>
      </div>
    </div>
  );
};

export default CategoryOrder;