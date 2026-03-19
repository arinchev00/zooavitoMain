import { Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useState, useEffect } from 'react';
import { getCurrentUser } from '../../api/user';

const RequireAuth = ({ children }) => {
  const { user, isAuthenticated, loading, logout } = useAuth();
  const [checking, setChecking] = useState(true);
  const [isBlocked, setIsBlocked] = useState(false);

  useEffect(() => {
    const checkUserStatus = async () => {
      if (!isAuthenticated) {
        setChecking(false);
        return;
      }

      try {
        // Проверяем актуальный статус пользователя на сервере
        const response = await getCurrentUser();
        const userData = response.data;
        
        if (userData && userData.enabled === false) {
          console.log('User is blocked, logging out...');
          setIsBlocked(true);
          logout();
        }
      } catch (error) {
        console.error('Error checking user status:', error);
        if (error.response?.status === 403 || error.response?.status === 401) {
          logout();
        }
      } finally {
        setChecking(false);
      }
    };

    checkUserStatus();
  }, [isAuthenticated, logout]);

  if (loading || checking) {
    return (
      <div className="container text-center py-5">
        <div className="spinner-border text-success" role="status">
          <span className="sr-only">Загрузка...</span>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (isBlocked) {
    return <Navigate to="/login?blocked=true" replace />;
  }

  return children;
};

export default RequireAuth;