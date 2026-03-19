import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { createAnnouncement, getAnnouncementById, updateAnnouncement } from '../../api/announcements';
import { getAllCategories } from '../../api/categories';
import './AnnouncementForm.css';

const AnnouncementForm = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEditing = !!id;

  const [formData, setFormData] = useState({
    title: '',
    price: '',
    description: '',
    subcategoryId: ''
  });

  const [categories, setCategories] = useState([]);
  const [subcategories, setSubcategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('');
  const [existingImages, setExistingImages] = useState([]);
  const [newImages, setNewImages] = useState([]);
  const [imagesToDelete, setImagesToDelete] = useState([]);
  const [imagePreviews, setImagePreviews] = useState([]);
  const [mainImageId, setMainImageId] = useState(null);
  const [newMainImageIndex, setNewMainImageIndex] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    loadCategories();
    if (isEditing) {
      loadAnnouncement();
    }
  }, [id]);

  const loadCategories = async () => {
    try {
      const response = await getAllCategories();
      setCategories(response.data);
    } catch (error) {
      console.error('Ошибка при загрузке категорий:', error);
    }
  };

  const loadAnnouncement = async () => {
    try {
      const response = await getAnnouncementById(id);
      const announcement = response.data;
      setFormData({
        title: announcement.title,
        price: announcement.price,
        description: announcement.description || '',
        subcategoryId: announcement.subcategory?.id || ''
      });
      setSelectedCategory(announcement.category?.id || '');
      
      if (announcement.images && announcement.images.length > 0) {
        setExistingImages(announcement.images);
        const mainImage = announcement.images.find(img => img.main === true);
        setMainImageId(mainImage ? mainImage.id : announcement.images[0].id);
      }
      
      if (announcement.category?.id) {
        loadSubcategories(announcement.category.id);
      }
    } catch (error) {
      console.error('Ошибка при загрузке объявления:', error);
      setError('Не удалось загрузить объявление');
    }
  };

  const loadSubcategories = async (categoryId) => {
    try {
      const response = await fetch(`/api/subcategories/by-category/${categoryId}`);
      const data = await response.json();
      setSubcategories(data);
    } catch (error) {
      console.error('Ошибка при загрузке подкатегорий:', error);
    }
  };

  const handleCategoryChange = (e) => {
    const categoryId = e.target.value;
    setSelectedCategory(categoryId);
    setFormData({ ...formData, subcategoryId: '' });
    if (categoryId) {
      loadSubcategories(categoryId);
    } else {
      setSubcategories([]);
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleImageChange = (e) => {
    const files = Array.from(e.target.files);
    setNewImages(prev => [...prev, ...files]);

    files.forEach((file, fileIndex) => {
      const reader = new FileReader();
      reader.onloadend = () => {
        setImagePreviews(prev => {
          const newPreviews = [...prev, reader.result];
          
          // Если это первое новое фото и нет других главных фото
          if (prev.length === 0 && existingImages.length === 0 && mainImageId === null) {
            setNewMainImageIndex(0);
          }
          
          return newPreviews;
        });
      };
      reader.readAsDataURL(file);
    });
  };

  const removeExistingImage = (imageId) => {
    setImagesToDelete(prev => [...prev, imageId]);
    setExistingImages(prev => prev.filter(img => img.id !== imageId));
    
    if (mainImageId === imageId) {
      const remainingImages = existingImages.filter(img => img.id !== imageId);
      if (remainingImages.length > 0) {
        setMainImageId(remainingImages[0].id);
      } else if (newImages.length > 0) {
        setMainImageId(null);
        setNewMainImageIndex(0);
      } else {
        setMainImageId(null);
      }
    }
  };

  const removeNewImage = (index) => {
    setNewImages(prev => prev.filter((_, i) => i !== index));
    setImagePreviews(prev => prev.filter((_, i) => i !== index));
    
    if (newMainImageIndex === index) {
      if (existingImages.length > 0) {
        setNewMainImageIndex(null);
        setMainImageId(existingImages[0].id);
      } else if (newImages.length > 1) {
        setNewMainImageIndex(0);
      } else {
        setNewMainImageIndex(null);
      }
    } else if (newMainImageIndex !== null && index < newMainImageIndex) {
      setNewMainImageIndex(prev => prev - 1);
    }
  };

  const setExistingAsMain = (imageId) => {
    setMainImageId(imageId);
    setNewMainImageIndex(null);
  };

  const setNewAsMain = (index) => {
    setNewMainImageIndex(index);
    setMainImageId(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const formDataToSend = new FormData();
      
      const announcementData = {
        title: formData.title,
        price: parseInt(formData.price),
        description: formData.description,
        subcategoryId: parseInt(formData.subcategoryId)
      };
      
      formDataToSend.append(
        'announcement', 
        new Blob([JSON.stringify(announcementData)], { type: 'application/json' })
      );
      
      if (isEditing && imagesToDelete.length > 0) {
        formDataToSend.append('imagesToDelete', JSON.stringify(imagesToDelete));
      }
      
      if (mainImageId) {
        formDataToSend.append('mainImageId', mainImageId.toString());
      }
      
      newImages.forEach(image => {
        formDataToSend.append('images', image);
      });

      if (isEditing) {
        await updateAnnouncement(id, formDataToSend);
      } else {
        await createAnnouncement(formDataToSend);
      }
      
      navigate('/announcements');
    } catch (error) {
      console.error('Ошибка при сохранении объявления:', error);
      setError('Не удалось сохранить объявление');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <div className="announcement-form-container">
        <h2 className="form-title">
          {isEditing ? 'Редактировать объявление' : 'Создать новое объявление'}
        </h2>

        {error && <div className="alert alert-danger">{error}</div>}

        <form onSubmit={handleSubmit} className="announcement-form">
          <div className="form-group">
            <label htmlFor="title">Заголовок <span className="required-star">*</span></label>
            <input
              type="text"
              id="title"
              name="title"
              value={formData.title}
              onChange={handleChange}
              className="form-control"
              placeholder="Например: Продаю котенка"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="price">Цена <span className="required-star">*</span></label>
            <input
              type="number"
              id="price"
              name="price"
              value={formData.price}
              onChange={handleChange}
              className="form-control"
              placeholder="Введите цену в рублях"
              min="0"
              required
            />
          </div>

          <div className="row">
            <div className="col-md-6">
              <div className="form-group">
                <label htmlFor="category">Категория <span className="required-star">*</span></label>
                <select
                  id="category"
                  className="form-control"
                  value={selectedCategory}
                  onChange={handleCategoryChange}
                  required
                >
                  <option value="">Выберите категорию</option>
                  {categories.map(cat => (
                    <option key={cat.id} value={cat.id}>{cat.title}</option>
                  ))}
                </select>
              </div>
            </div>

            <div className="col-md-6">
              <div className="form-group">
                <label htmlFor="subcategoryId">Подкатегория <span className="required-star">*</span></label>
                <select
                  id="subcategoryId"
                  name="subcategoryId"
                  className="form-control"
                  value={formData.subcategoryId}
                  onChange={handleChange}
                  disabled={!selectedCategory}
                  required
                >
                  <option value="">Выберите подкатегорию</option>
                  {subcategories.map(sub => (
                    <option key={sub.id} value={sub.id}>{sub.title}</option>
                  ))}
                </select>
              </div>
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="description">Описание</label>
            <textarea
              id="description"
              name="description"
              value={formData.description}
              onChange={handleChange}
              className="form-control"
              rows="5"
              placeholder="Подробно опишите ваше предложение (необязательно)"
            />
          </div>

          <div className="form-group">
            <label>Фотографии</label>
            
            {/* Существующие изображения */}
            {isEditing && existingImages.length > 0 && (
              <div className="existing-images">
                <h6>Текущие фотографии:</h6>
                <div className="image-previews">
                  {existingImages.map(image => (
                    <div 
                      key={image.id} 
                      className={`image-preview ${mainImageId === image.id ? 'main-image' : ''}`}
                    >
                      <img 
                        src={`data:${image.contentType};base64,${image.base64Image}`} 
                        alt={image.originalFileName} 
                      />
                      <div style={{ 
                        position: 'absolute', 
                        top: '5px', 
                        left: '5px', 
                        display: 'flex', 
                        gap: '5px',
                        zIndex: 10
                      }}>
                        {mainImageId !== image.id && (
                          <button
                            type="button"
                            onClick={() => setExistingAsMain(image.id)}
                            style={{
                              width: '30px',
                              height: '30px',
                              backgroundColor: '#ffc107',
                              color: '#333',
                              border: 'none',
                              borderRadius: '50%',
                              cursor: 'pointer',
                              display: 'flex',
                              alignItems: 'center',
                              justifyContent: 'center',
                              fontSize: '18px',
                              fontWeight: 'bold'
                            }}
                            title="Сделать главным"
                          >
                            ☆
                          </button>
                        )}
                        {mainImageId === image.id && (
                          <span style={{
                            width: '30px',
                            height: '30px',
                            backgroundColor: '#28a745',
                            color: 'white',
                            borderRadius: '50%',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            fontSize: '18px',
                            fontWeight: 'bold'
                          }} title="Главное фото">
                            ★
                          </span>
                        )}
                      </div>
                      <button
                        type="button"
                        className="btn-remove"
                        onClick={() => removeExistingImage(image.id)}
                      >
                        <i className="fas fa-times"></i>
                      </button>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Загрузка новых изображений */}
            <div className="image-upload-container">
              <input
                type="file"
                id="images"
                accept="image/*"
                onChange={handleImageChange}
                multiple
                className="image-input"
              />
              <label htmlFor="images" className="image-upload-label">
                <i className="fas fa-cloud-upload-alt"></i>
                <span>Выберите файлы или перетащите их сюда</span>
              </label>
            </div>
            <small className="form-text text-muted">
              Можно выбрать несколько изображений. Поддерживаются JPG, PNG, GIF.
            </small>
          </div>

          {/* Превью новых изображений */}
          {imagePreviews.length > 0 && (
            <div className="new-images">
              <h6>Новые фотографии:</h6>
              <div className="image-previews">
                {imagePreviews.map((preview, index) => (
                  <div 
                    key={index} 
                    className={`image-preview ${newMainImageIndex === index ? 'main-image' : ''}`}
                  >
                    <img src={preview} alt={`Preview ${index + 1}`} />
                    
                    {/* Кнопки управления для нового фото */}
                    <div style={{ 
                      position: 'absolute', 
                      top: '5px', 
                      left: '5px', 
                      display: 'flex', 
                      gap: '5px',
                      zIndex: 10
                    }}>
                      {newMainImageIndex !== index && (
                        <button
                          type="button"
                          onClick={() => setNewAsMain(index)}
                          style={{
                            width: '30px',
                            height: '30px',
                            backgroundColor: '#ffc107',
                            color: '#333',
                            border: 'none',
                            borderRadius: '50%',
                            cursor: 'pointer',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            fontSize: '18px',
                            fontWeight: 'bold'
                          }}
                          title="Сделать главным"
                        >
                          ☆
                        </button>
                      )}
                      {newMainImageIndex === index && (
                        <span style={{
                          width: '30px',
                          height: '30px',
                          backgroundColor: '#28a745',
                          color: 'white',
                          borderRadius: '50%',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          fontSize: '18px',
                          fontWeight: 'bold'
                        }} title="Главное фото">
                          ★
                        </span>
                      )}
                    </div>

                    {/* Кнопка удаления */}
                    <button
                      type="button"
                      onClick={() => removeNewImage(index)}
                      style={{
                        position: 'absolute',
                        top: '5px',
                        right: '5px',
                        width: '30px',
                        height: '30px',
                        backgroundColor: '#dc3545',
                        color: 'white',
                        border: 'none',
                        borderRadius: '50%',
                        cursor: 'pointer',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontSize: '14px',
                        fontWeight: 'bold',
                        zIndex: 10
                      }}
                      title="Удалить"
                    >
                      ✕
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}

          <div className="form-actions">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => navigate('/announcements')}
            >
              Отмена
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading}
            >
              {loading ? 'Сохранение...' : (isEditing ? 'Сохранить изменения' : 'Опубликовать')}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AnnouncementForm;