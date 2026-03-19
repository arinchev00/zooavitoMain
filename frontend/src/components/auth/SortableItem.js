import React from 'react';
import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';

const SortableItem = ({ id, category, index, isHidden, onToggleVisibility }) => {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({ 
    id: id,
    disabled: isHidden, // Нельзя перетаскивать скрытые категории
  });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : isHidden ? 0.5 : 1,
    zIndex: isDragging ? 1000 : 'auto',
    position: 'relative',
    backgroundColor: isHidden ? '#f8f9fa' : 'white',
  };

  const handleToggleClick = (e) => {
    e.stopPropagation(); // Предотвращаем начало перетаскивания
    onToggleVisibility(id);
  };

  return (
    <div
      ref={setNodeRef}
      style={style}
      className={`category-item ${isDragging ? 'dragging' : ''} ${isHidden ? 'hidden' : ''}`}
      {...attributes}
      {...listeners}
    >
      <div className="drag-handle" style={{ opacity: isHidden ? 0.3 : 1 }}>
        <i className="fas fa-grip-vertical"></i>
      </div>
      <div className="category-info">
        <span className="category-title" style={{ textDecoration: isHidden ? 'line-through' : 'none' }}>
          {category.title}
        </span>
        <span className="category-position">Позиция: {index + 1}</span>
      </div>
      <div className="category-visibility">
        <button
          onClick={handleToggleClick}
          className={`btn btn-sm ${isHidden ? 'btn-success' : 'btn-warning'}`}
          title={isHidden ? 'Показать категорию' : 'Скрыть категорию'}
        >
          <i className={`fas ${isHidden ? 'fa-eye' : 'fa-eye-slash'}`}></i>
        </button>
      </div>
    </div>
  );
};

export default SortableItem;