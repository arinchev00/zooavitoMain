import React, { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { useCategories } from '../../context/CategoryContext';
import { useCategoryOrder } from '../../context/CategoryOrderContext';
import { getSubcategoriesByCategoryId } from '../../api/categories';

const CategoryDropdown = () => {
  const { categories } = useCategories();
  const { getVisibleCategories } = useCategoryOrder();
  const [visibleCategories, setVisibleCategories] = useState([]);
  const [activeCategory, setActiveCategory] = useState(null);
  const [subcategories, setSubcategories] = useState({});
  const timeoutRef = useRef(null);

  useEffect(() => {
    if (categories) {
      const visible = getVisibleCategories(categories);
      setVisibleCategories(visible);
    }
  }, [categories, getVisibleCategories]);

  const loadSubcategories = async (categoryId) => {
    if (!subcategories[categoryId]) {
      try {
        const response = await getSubcategoriesByCategoryId(categoryId);
        setSubcategories(prev => ({
          ...prev,
          [categoryId]: response.data
        }));
      } catch (error) {
        console.error('Ошибка при загрузке подкатегорий:', error);
      }
    }
  };

  const handleMouseEnter = (categoryId) => {
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
    }
    setActiveCategory(categoryId);
    loadSubcategories(categoryId);
  };

  const handleMouseLeave = () => {
    timeoutRef.current = setTimeout(() => {
      setActiveCategory(null);
    }, 200);
  };

  return (
    <ul className="navbar-nav mr-auto">
      {visibleCategories.map(category => (
        <li 
          key={category.id} 
          className="nav-item dropdown"
          onMouseEnter={() => handleMouseEnter(category.id)}
          onMouseLeave={handleMouseLeave}
        >
          <Link
            to={`/announcements?categoryId=${category.id}`}
            className="nav-link dropdown-toggle"
          >
            {category.title}
          </Link>
          {activeCategory === category.id && subcategories[category.id]?.length > 0 && (
            <div className="dropdown-menu show">
              {subcategories[category.id].map(sub => (
                <Link
                  key={sub.id}
                  to={`/announcements?categoryId=${category.id}&subcategoryId=${sub.id}`}
                  className="dropdown-item"
                >
                  {sub.title}
                </Link>
              ))}
            </div>
          )}
        </li>
      ))}
    </ul>
  );
};

export default CategoryDropdown;