import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useCategories } from '../../context/CategoryContext';
import { useCategoryOrder } from '../../context/CategoryOrderContext';
import { getAllCategories, createCategory, updateCategory, deleteCategory } from '../../api/categories';
import { getSubcategoriesByCategoryId, createSubcategory, updateSubcategory, deleteSubcategory } from '../../api/subcategories';
import UserManagement from './UserManagement';
import './AdminPanel.css';

const AdminPanel = () => {
  const navigate = useNavigate();
  const { user, isAuthenticated, refreshUserData } = useAuth();
  const { refreshCategories } = useCategories();
  const { categoryOrder, hiddenCategories } = useCategoryOrder();
  
  const [categories, setCategories] = useState([]);
  const [orderedCategories, setOrderedCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [editingCategory, setEditingCategory] = useState(null);
  const [editingSubcategory, setEditingSubcategory] = useState(null);
  const [newCategoryTitle, setNewCategoryTitle] = useState('');
  const [newSubcategory, setNewSubcategory] = useState({ title: '', categoryId: '' });
  const [expandedCategories, setExpandedCategories] = useState({});

  // Проверка прав доступа
useEffect(() => {
  if (!isAuthenticated || user?.role !== 'ROLE_ADMIN') {
    navigate('/announcements');
  }
}, [isAuthenticated, user, navigate]);

  useEffect(() => {
    loadCategories();
  }, []);

  // Применяем порядок к категориям при изменении categories или categoryOrder
  useEffect(() => {
    if (categories.length > 0) {
      let sorted = [...categories];
      
      if (categoryOrder.length > 0) {
        sorted.sort((a, b) => {
          const indexA = categoryOrder.indexOf(a.id);
          const indexB = categoryOrder.indexOf(b.id);
          
          if (indexA === -1 && indexB === -1) return a.id - b.id;
          if (indexA === -1) return 1;
          if (indexB === -1) return -1;
          return indexA - indexB;
        });
      }
      
      setOrderedCategories(sorted);
    }
  }, [categories, categoryOrder]);

  const loadCategories = async () => {
    try {
      setLoading(true);
      const response = await getAllCategories();
      setCategories(response.data);
      
      // Загружаем подкатегории для всех категорий
      const categoriesWithSubs = await Promise.all(
        response.data.map(async (cat) => {
          const subsResponse = await getSubcategoriesByCategoryId(cat.id);
          return { ...cat, subcategories: subsResponse.data };
        })
      );
      setCategories(categoriesWithSubs);
    } catch (error) {
      console.error('Ошибка при загрузке категорий:', error);
      setError('Не удалось загрузить категории');
    } finally {
      setLoading(false);
    }
  };

  // Функция для обновления всех данных после изменений
  const refreshAllData = async () => {
    await loadCategories();
    if (refreshCategories) {
      await refreshCategories();
    }
  };

  const handleCreateCategory = async (e) => {
    e.preventDefault();
    if (!newCategoryTitle.trim()) return;

    try {
      setLoading(true);
      await createCategory({ title: newCategoryTitle });
      setNewCategoryTitle('');
      setSuccess('Категория успешно создана');
      await refreshAllData();
      setTimeout(() => setSuccess(''), 3000);
    } catch (error) {
      console.error('Ошибка при создании категории:', error);
      setError('Не удалось создать категорию');
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateCategory = async (id) => {
    if (!editingCategory?.title.trim()) return;

    try {
      setLoading(true);
      await updateCategory(id, { title: editingCategory.title });
      setEditingCategory(null);
      setSuccess('Категория успешно обновлена');
      await refreshAllData();
      setTimeout(() => setSuccess(''), 3000);
    } catch (error) {
      console.error('Ошибка при обновлении категории:', error);
      setError('Не удалось обновить категорию');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteCategory = async (id) => {
    if (!window.confirm('Вы уверены, что хотите удалить эту категорию? Все подкатегории также будут удалены.')) {
      return;
    }

    try {
      setLoading(true);
      await deleteCategory(id);
      setSuccess('Категория успешно удалена');
      await refreshAllData();
      setTimeout(() => setSuccess(''), 3000);
    } catch (error) {
      console.error('Ошибка при удалении категории:', error);
      setError('Не удалось удалить категорию');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateSubcategory = async (categoryId) => {
    if (!newSubcategory.title.trim()) return;

    try {
      setLoading(true);
      await createSubcategory({
        title: newSubcategory.title,
        categoryId: categoryId
      });
      setNewSubcategory({ title: '', categoryId: '' });
      setSuccess('Подкатегория успешно создана');
      await refreshAllData();
      setTimeout(() => setSuccess(''), 3000);
    } catch (error) {
      console.error('Ошибка при создании подкатегории:', error);
      setError('Не удалось создать подкатегорию');
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateSubcategory = async (id) => {
    if (!editingSubcategory?.title.trim()) return;

    try {
      setLoading(true);
      await updateSubcategory(id, {
        title: editingSubcategory.title,
        categoryId: editingSubcategory.categoryId
      });
      setEditingSubcategory(null);
      setSuccess('Подкатегория успешно обновлена');
      await refreshAllData();
      setTimeout(() => setSuccess(''), 3000);
    } catch (error) {
      console.error('Ошибка при обновлении подкатегории:', error);
      setError('Не удалось обновить подкатегорию');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteSubcategory = async (id) => {
    if (!window.confirm('Вы уверены, что хотите удалить эту подкатегорию?')) {
      return;
    }

    try {
      setLoading(true);
      await deleteSubcategory(id);
      setSuccess('Подкатегория успешно удалена');
      await refreshAllData();
      setTimeout(() => setSuccess(''), 3000);
    } catch (error) {
      console.error('Ошибка при удалении подкатегории:', error);
      setError('Не удалось удалить подкатегорию');
    } finally {
      setLoading(false);
    }
  };

  const toggleCategory = (categoryId) => {
    setExpandedCategories(prev => ({
      ...prev,
      [categoryId]: !prev[categoryId]
    }));
  };

if (!isAuthenticated || user?.role !== 'ROLE_ADMIN') {
  return null;
}

  return (
    <div className="admin-panel">
      <div className="container">
        <h1 className="admin-title">Панель админа</h1>
        
        {error && <div className="alert alert-danger">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}

        {/* Форма создания категории */}
        <div className="admin-section">
          <h2>Создать новую категорию</h2>
          <form onSubmit={handleCreateCategory} className="admin-form">
            <div className="form-group">
              <input
                type="text"
                value={newCategoryTitle}
                onChange={(e) => setNewCategoryTitle(e.target.value)}
                placeholder="Название категории"
                className="form-control"
                required
              />
            </div>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              Создать категорию
            </button>
          </form>
        </div>

        {/* Ссылка на страницу управления порядком категорий в шапке */}
        <div className="admin-section order-navigation-section">
          <div className="order-navigation">
            <h2>Управление отображением в шапке</h2>
            <p className="text-muted">
              Измените порядок отображения категорий в верхней панели навигации сайта
            </p>
            <button
              onClick={() => navigate('/admin/category-order')}
              className="btn btn-success btn-lg"
            >
              <i className="fas fa-arrows-alt"></i>
              Изменить порядок категорий в шапке
            </button>
          </div>
        </div>

        {/* Список категорий */}
        <div className="admin-section">
          <h2>Управление категориями</h2>
          {loading ? (
            <div className="text-center py-5">
              <div className="spinner-border text-success" role="status">
                <span className="sr-only">Загрузка...</span>
              </div>
            </div>
          ) : (
            <div className="categories-list">
              {orderedCategories.map(category => (
                <div key={category.id} className="category-item">
                  <div className="category-header">
                    {editingCategory?.id === category.id ? (
                      <div className="edit-form">
                        <input
                          type="text"
                          value={editingCategory.title}
                          onChange={(e) => setEditingCategory({ ...editingCategory, title: e.target.value })}
                          className="form-control"
                          autoFocus
                        />
                        <button
                          onClick={() => handleUpdateCategory(category.id)}
                          className="btn btn-sm btn-success"
                        >
                          Сохранить
                        </button>
                        <button
                          onClick={() => setEditingCategory(null)}
                          className="btn btn-sm btn-secondary"
                        >
                          Отмена
                        </button>
                      </div>
                    ) : (
                      <>
                        <div className="category-info">
                          <span className="category-title">{category.title}</span>
                          <span className="category-id">
                            ID: {category.id} | 
                            Позиция: {orderedCategories.findIndex(c => c.id === category.id) + 1}
                          </span>
                        </div>
                        <div className="category-actions">
                          <button
                            onClick={() => toggleCategory(category.id)}
                            className="btn btn-sm btn-info"
                          >
                            {expandedCategories[category.id] ? 'Скрыть подкатегории' : 'Показать подкатегории'}
                          </button>
                          <button
                            onClick={() => setEditingCategory({ id: category.id, title: category.title })}
                            className="btn btn-sm btn-warning"
                          >
                            <i className="fas fa-edit"></i>
                          </button>
                          <button
                            onClick={() => handleDeleteCategory(category.id)}
                            className="btn btn-sm btn-danger"
                          >
                            <i className="fas fa-trash"></i>
                          </button>
                        </div>
                      </>
                    )}
                  </div>

                  {/* Подкатегории */}
                  {expandedCategories[category.id] && (
                    <div className="subcategories-section">
                      <h4>Подкатегории</h4>
                      
                      {/* Форма создания подкатегории */}
                      <div className="create-subcategory-form">
                        <input
                          type="text"
                          value={newSubcategory.categoryId === category.id ? newSubcategory.title : ''}
                          onChange={(e) => setNewSubcategory({
                            title: e.target.value,
                            categoryId: category.id
                          })}
                          placeholder="Название подкатегории"
                          className="form-control"
                        />
                        <button
                          onClick={() => handleCreateSubcategory(category.id)}
                          className="btn btn-sm btn-primary"
                          disabled={!newSubcategory.title || newSubcategory.categoryId !== category.id}
                        >
                          Добавить
                        </button>
                      </div>

                      {/* Список подкатегорий */}
                      <div className="subcategories-list">
                        {category.subcategories?.map(sub => (
                          <div key={sub.id} className="subcategory-item">
                            {editingSubcategory?.id === sub.id ? (
                              <div className="edit-form">
                                <input
                                  type="text"
                                  value={editingSubcategory.title}
                                  onChange={(e) => setEditingSubcategory({ ...editingSubcategory, title: e.target.value })}
                                  className="form-control"
                                  autoFocus
                                />
                                <button
                                  onClick={() => handleUpdateSubcategory(sub.id)}
                                  className="btn btn-sm btn-success"
                                >
                                  Сохранить
                                </button>
                                <button
                                  onClick={() => setEditingSubcategory(null)}
                                  className="btn btn-sm btn-secondary"
                                >
                                  Отмена
                                </button>
                              </div>
                            ) : (
                              <>
                                <span className="subcategory-title">{sub.title}</span>
                                <div className="subcategory-actions">
                                  <button
                                    onClick={() => setEditingSubcategory({
                                      id: sub.id,
                                      title: sub.title,
                                      categoryId: category.id
                                    })}
                                    className="btn btn-sm btn-warning"
                                  >
                                    <i className="fas fa-edit"></i>
                                  </button>
                                  <button
                                    onClick={() => handleDeleteSubcategory(sub.id)}
                                    className="btn btn-sm btn-danger"
                                  >
                                    <i className="fas fa-trash"></i>
                                  </button>
                                </div>
                              </>
                            )}
                          </div>
                        ))}
                        {(!category.subcategories || category.subcategories.length === 0) && (
                          <p className="text-muted">Нет подкатегорий</p>
                        )}
                      </div>
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Новая секция: Управление пользователями */}
        <div className="admin-section">
          <h2>Управление пользователями</h2>
          <UserManagement />
        </div>
      </div>
    </div>
  );
};

export default AdminPanel;