import React, { createContext, useState, useContext, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';
import { getCurrentUser } from '../api/user';

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initAuth = async () => {
      const token = localStorage.getItem('token');
      const userData = localStorage.getItem('userData');

      if (token && userData) {
        try {
          // Проверяем валидность токена
          const decoded = jwtDecode(token);
          const now = Date.now() / 1000;

          if (decoded.exp < now) {
            // Токен просрочен - чистим
            console.log('Token expired, cleaning up...');
            localStorage.removeItem('token');
            localStorage.removeItem('userData');
            setLoading(false);
            return;
          }

          // Парсим данные из localStorage
          const parsedUserData = JSON.parse(userData);

          // Временно устанавливаем пользователя из localStorage
          setUser({
            id: parsedUserData.id,
            email: decoded.sub,
            fullName: parsedUserData.fullName,
            phone: parsedUserData.phone,
            role: parsedUserData.role,
            enabled: parsedUserData.enabled,
            token: token,
          });

          // Обновляем данные с сервера
          try {
            await refreshUserData();
          } catch (error) {
            console.error('Failed to refresh user data:', error);
            // Если не удалось обновить, оставляем данные из localStorage
          }

        } catch (error) {
          console.error('Ошибка при загрузке пользователя:', error);
          localStorage.removeItem('token');
          localStorage.removeItem('userData');
        }
      }
      setLoading(false);
    };

    initAuth();
  }, []);

  const login = (token, userData) => {
    console.log('🔵 AuthProvider - login:', userData);
    localStorage.setItem('token', token);
    localStorage.setItem('userData', JSON.stringify(userData));

    try {
      const decoded = jwtDecode(token);
      setUser({
        id: userData.id,
        email: decoded.sub,
        fullName: userData.fullName,
        phone: userData.phone,
        role: userData.role,
        enabled: userData.enabled,
        token: token,
      });
    } catch (error) {
      console.error('Ошибка при декодировании токена:', error);
    }
  };

  const refreshUserData = async () => {
    try {
      console.log('🔄 REFRESH USER DATA: starting...');
      const token = localStorage.getItem('token');

      if (!token) {
        console.log('No token found, skipping refresh');
        return;
      }

      const response = await getCurrentUser();
      const userData = response.data;
      console.log('User data from server:', userData);

      // Определяем роль (берем первую из массива)
      const role = userData.roles && userData.roles.length > 0
        ? userData.roles[0]
        : 'ROLE_USER';

      // Создаем обновленный объект пользователя
      const updatedUser = {
        id: userData.id,
        email: userData.email,
        fullName: userData.fullName,
        phone: userData.telephoneNumber,
        role: role,
        enabled: userData.enabled,
        token: token,
      };

      console.log('Updated user object:', updatedUser);

      // Обновляем данные в контексте
      setUser(updatedUser);

      // Обновляем localStorage
      localStorage.setItem('userData', JSON.stringify({
        id: userData.id,
        email: userData.email,
        fullName: userData.fullName,
        phone: userData.telephoneNumber,
        role: role,
        enabled: userData.enabled
      }));

      console.log('User data refreshed successfully');
    } catch (error) {
      console.error('Ошибка при обновлении данных пользователя:', error);
      // Если ошибка 401, значит токен невалидный - выходим
      if (error.response?.status === 401) {
        console.log('Token invalid, logging out');
        logout();
      }
      throw error;
    }
  };

  const updateUserData = (updatedData) => {
    setUser(prev => {
      const newUser = { ...prev, ...updatedData };
      localStorage.setItem('userData', JSON.stringify({
        id: newUser.id,
        email: newUser.email,
        fullName: newUser.fullName,
        phone: newUser.phone,
        role: newUser.role,
        enabled: newUser.enabled
      }));
      return newUser;
    });
  };

  const logout = () => {
    console.log('🔴 Logging out...');
    localStorage.removeItem('token');
    localStorage.removeItem('userData');
    setUser(null);
  };

  const value = {
    user,
    login,
    logout,
    updateUserData,
    refreshUserData,
    loading,
    isAuthenticated: !!user && !!localStorage.getItem('token'), // Проверяем оба условия
  };

  // Показываем спиннер пока загружаемся
  if (loading) {
    return <div style={{
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      height: '100vh'
    }}>Loading...</div>;
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};