import React, { createContext, useState, useContext, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';
import { getCurrentUser } from '../api/user';

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

useEffect(() => {
  const token = localStorage.getItem('token');
  const userData = localStorage.getItem('userData');
  
  if (token && userData) {
    try {
      const decoded = jwtDecode(token);
      const parsedUserData = JSON.parse(userData);
      setUser({
        id: parsedUserData.id,
        email: decoded.sub,
        fullName: parsedUserData.fullName,
        phone: parsedUserData.phone,
        role: parsedUserData.role,
        enabled: parsedUserData.enabled, // Добавляем поле enabled
        token: token,
      });
    } catch (error) {
      console.error('Ошибка при загрузке пользователя:', error);
      localStorage.removeItem('token');
      localStorage.removeItem('userData');
    }
  }
  setLoading(false);
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
        token: token,
      });
    } catch (error) {
      console.error('Ошибка при декодировании токена:', error);
    }
  };

  // ЕДИНСТВЕННЫЙ МЕТОД refreshUserData
 const refreshUserData = async () => {
  try {
    console.log('🔄 REFRESH USER DATA: starting...');
    const response = await getCurrentUser();
    const userData = response.data;
    console.log('User data from server:', userData);
    
    // Получаем текущий токен
    const token = localStorage.getItem('token');
    
    // Определяем роль (берем первую из массива)
    const role = userData.roles && userData.roles.length > 0 
      ? userData.roles[0] 
      : 'ROLE_USER';
    
    // Создаем обновленный объект пользователя с enabled
    const updatedUser = {
      id: userData.id,
      email: userData.email,
      fullName: userData.fullName,
      phone: userData.telephoneNumber,
      role: role,
      enabled: userData.enabled, // Добавляем поле enabled
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
      enabled: userData.enabled // Добавляем в localStorage
    }));
    
    console.log('User data refreshed successfully');
  } catch (error) {
    console.error('Ошибка при обновлении данных пользователя:', error);
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
        role: newUser.role
      }));
      return newUser;
    });
  };

  const logout = () => {
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
    isAuthenticated: !!user,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};