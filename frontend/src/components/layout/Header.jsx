import React, { useState, useRef, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import CategoryDropdown from '../categories/CategoryDropdown';

const Header = () => {
  const { user, isAuthenticated, logout, refreshUserData } = useAuth();
  const navigate = useNavigate();
  const [showUserMenu, setShowUserMenu] = useState(false);
  const menuRef = useRef(null);
  const timeoutRef = useRef(null);
  const hasRefreshed = useRef(false); // Добавляем ref для отслеживания

  // Отладка - логируем изменения пользователя
  useEffect(() => {
    console.log('👤 HEADER: current user from context', user);
    console.log('👤 HEADER: user role', user?.role);
    console.log('👤 HEADER: is admin?', user?.role === 'ROLE_ADMIN');
  }, [user]);

  useEffect(() => {
    // Обновляем данные только один раз при монтировании
    if (isAuthenticated && !hasRefreshed.current) {
      hasRefreshed.current = true;
      refreshUserData();
    }
  }, [isAuthenticated, refreshUserData]); // Зависимости оставляем, но используем ref

  // Закрытие меню при клике вне его
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setShowUserMenu(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const handleMouseEnter = () => {
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
    }
    setShowUserMenu(true);
  };

  const handleMouseLeave = () => {
    timeoutRef.current = setTimeout(() => {
      setShowUserMenu(false);
    }, 200);
  };

  const handleLogout = () => {
    logout();
    navigate('/home');
  };

  const formatPhone = (phone) => {
    if (!phone) return '';
    const cleaned = phone.replace(/\D/g, '');
    if (cleaned.length === 11) {
      return `+${cleaned[0]} (${cleaned.slice(1, 4)}) ${cleaned.slice(4, 7)}-${cleaned.slice(7, 9)}-${cleaned.slice(9, 11)}`;
    }
    return phone;
  };

  const getInitials = () => {
    if (!user) return 'U';
    
    if (user?.fullName) {
      return user.fullName
        .split(' ')
        .map(word => word[0])
        .join('')
        .toUpperCase()
        .slice(0, 2);
    }
    return user?.email?.charAt(0).toUpperCase() || 'U';
  };

  const getDisplayName = () => {
    if (!user) return 'Пользователь';
    
    if (user?.fullName) {
      const nameParts = user.fullName.split(' ');
      if (nameParts.length > 1) {
        return `${nameParts[0]} ${nameParts[1].charAt(0)}.`;
      }
      return user.fullName;
    }
    return user?.email?.split('@')[0] || 'Пользователь';
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-custom">
      <div className="container">
        <Link className="navbar-brand" to="/home">
          <i className="fas fa-paw"></i>
          ZooAvito
        </Link>

        <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarMain">
          <span className="navbar-toggler-icon"></span>
        </button>

        <div className="collapse navbar-collapse" id="navbarMain">
          <CategoryDropdown />

          {/* Кнопка для админа */}
         {isAuthenticated && user?.role === 'ROLE_ADMIN' && (
  <div className="mr-3">
    <Link to="/admin" className="btn btn-outline-light">
      <i className="fas fa-cog"></i> Панель Админа
    </Link>
  </div>
)}

          {!isAuthenticated ? (
            <div>
              <Link to="/login" className="btn btn-outline-light mr-2">Вход</Link>
              <Link to="/register" className="btn btn-light">Регистрация</Link>
            </div>
          ) : (
            <div className="d-flex align-items-center">
              <div 
                ref={menuRef}
                className="user-info-wrapper"
                onMouseEnter={handleMouseEnter}
                onMouseLeave={handleMouseLeave}
                style={{ position: 'relative' }}
              >
                <div className="user-info d-flex align-items-center" style={{ cursor: 'pointer' }}>
                  <div 
                    className="user-avatar mr-2"
                    style={{
                      width: '40px',
                      height: '40px',
                      borderRadius: '50%',
                      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      color: 'white',
                      fontWeight: 'bold',
                      fontSize: '16px'
                    }}
                  >
                    {getInitials()}
                  </div>
                  <div className="user-details">
                    <div className="user-name" style={{ fontWeight: '500', lineHeight: '1.2' }}>
                      {getDisplayName()}
                      {user?.role === 'ROLE_ADMIN' && (
                        <span 
                          className="admin-badge ml-2"
                          style={{
                            fontSize: '10px',
                            background: '#ffc107',
                            color: '#000',
                            padding: '2px 6px',
                            borderRadius: '10px',
                            fontWeight: 'bold'
                          }}
                        >
                          ADMIN
                        </span>
                      )}
                    </div>
                    {user?.phone && (
                      <div className="user-phone" style={{ fontSize: '11px', opacity: '0.8' }}>
                        <i className="fas fa-phone-alt mr-1" style={{ fontSize: '9px' }}></i>
                        {formatPhone(user.phone)}
                      </div>
                    )}
                  </div>
                  <i className="fas fa-chevron-down ml-2" style={{ fontSize: '12px', opacity: '0.7' }}></i>
                </div>

                {/* Выпадающее меню */}
                {showUserMenu && (
                  <div 
                    className="user-dropdown-menu"
                    style={{
                      position: 'absolute',
                      top: '100%',
                      right: '0',
                      background: 'white',
                      borderRadius: '8px',
                      boxShadow: '0 5px 20px rgba(0,0,0,0.15)',
                      minWidth: '250px',
                      marginTop: '10px',
                      zIndex: 1000,
                      overflow: 'hidden'
                    }}
                  >
                    <div className="dropdown-header" style={{ 
                      padding: '16px', 
                      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                      color: 'white'
                    }}>
                      <div style={{ fontWeight: '600', fontSize: '16px', marginBottom: '4px' }}>
                        {user?.fullName || 'Пользователь'}
                      </div>
                      <div style={{ fontSize: '13px', opacity: '0.9' }}>
                        <i className="fas fa-envelope mr-2"></i>
                        {user?.email}
                      </div>
                      {user?.phone && (
                        <div style={{ fontSize: '13px', opacity: '0.9', marginTop: '4px' }}>
                          <i className="fas fa-phone-alt mr-2"></i>
                          {formatPhone(user.phone)}
                        </div>
                      )}
                    </div>
                    
                    {/* Личный кабинет */}
                    <Link 
                      to="/profile" 
                      className="dropdown-item"
                      onClick={() => setShowUserMenu(false)}
                      style={{
                        padding: '12px 16px',
                        display: 'block',
                        color: '#333',
                        textDecoration: 'none',
                        borderBottom: '1px solid #f0f0f0',
                        transition: 'background 0.2s'
                      }}
                      onMouseEnter={(e) => e.target.style.background = '#f8f9fa'}
                      onMouseLeave={(e) => e.target.style.background = 'white'}
                    >
                      <i className="fas fa-user mr-3" style={{ width: '20px', color: '#667eea' }}></i>
                      Личный кабинет
                    </Link>
                    
                    {/* Мои объявления */}
                    <Link 
                      to="/my-announcements" 
                      className="dropdown-item"
                      onClick={() => setShowUserMenu(false)}
                      style={{
                        padding: '12px 16px',
                        display: 'block',
                        color: '#333',
                        textDecoration: 'none',
                        borderBottom: '1px solid #f0f0f0',
                        transition: 'background 0.2s'
                      }}
                      onMouseEnter={(e) => e.target.style.background = '#f8f9fa'}
                      onMouseLeave={(e) => e.target.style.background = 'white'}
                    >
                      <i className="fas fa-list mr-3" style={{ width: '20px', color: '#667eea' }}></i>
                      Мои объявления
                    </Link>
                    
                    {/* Выход */}
                    <button 
                      onClick={() => {
                        setShowUserMenu(false);
                        handleLogout();
                      }}
                      className="dropdown-item"
                      style={{
                        padding: '12px 16px',
                        width: '100%',
                        textAlign: 'left',
                        border: 'none',
                        background: 'white',
                        color: '#dc3545',
                        cursor: 'pointer',
                        transition: 'background 0.2s'
                      }}
                      onMouseEnter={(e) => e.target.style.background = '#fff5f5'}
                      onMouseLeave={(e) => e.target.style.background = 'white'}
                    >
                      <i className="fas fa-sign-out-alt mr-3" style={{ width: '20px' }}></i>
                      Выйти
                    </button>
                  </div>
                )}
              </div>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Header;