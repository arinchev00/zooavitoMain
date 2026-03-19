import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
// import { GoogleReCaptchaProvider } from 'react-google-recaptcha-v3'; // 👈 УДАЛИТЬ
import { AuthProvider } from './context/AuthContext';
import { CategoryProvider } from './context/CategoryContext';
import { CategoryOrderProvider } from './context/CategoryOrderContext';
import RequireAuth from './components/common/RequireAuth';
import Header from './components/layout/Header';
import Footer from './components/layout/Footer';
import Home from './components/Home';
import AnnouncementsPage from './components/announcements/AnnouncementsPage';
import AnnouncementForm from './components/announcements/AnnouncementForm';
import AnnouncementDetail from './components/announcements/AnnouncementDetail';
import Register from './components/auth/Register';
import Login from './components/auth/Login';
import AdminPanel from './components/auth/AdminPanel';
import './App.css';
import './styles/main.css';
import CategoryOrder from './components/auth/CategoryOrder';
import MyAnnouncements from './components/announcements/MyAnnouncements';
import Profile from './components/profile/Profile';

function App() {
  return (
    <Router>
      {/* 👇 УБИРАЕМ GoogleReCaptchaProvider - он не нужен для v2 */}
      <AuthProvider>
        <CategoryProvider>
          <CategoryOrderProvider>
            <div className="App">
              <Header />
              <Routes>
                {/* Публичные маршруты */}
                <Route path="/" element={<Navigate to="/home" replace />} />
                <Route path="/home" element={<Home />} />
                <Route path="/announcements" element={<AnnouncementsPage />} />
                <Route path="/announcements/:id" element={<AnnouncementDetail />} />
                <Route path="/register" element={<Register />} />
                <Route path="/login" element={<Login />} />

                {/* Защищенные маршруты */}
                <Route 
                  path="/announcements/create" 
                  element={
                    <RequireAuth>
                      <AnnouncementForm />
                    </RequireAuth>
                  } 
                />
                <Route 
                  path="/announcements/edit/:id" 
                  element={
                    <RequireAuth>
                      <AnnouncementForm />
                    </RequireAuth>
                  } 
                />
                <Route 
                  path="/my-announcements" 
                  element={
                    <RequireAuth>
                      <MyAnnouncements />
                    </RequireAuth>
                  } 
                />
                <Route 
                  path="/profile" 
                  element={
                    <RequireAuth>
                      <Profile />
                    </RequireAuth>
                  } 
                />
                <Route 
                  path="/admin" 
                  element={
                    <RequireAuth>
                      <AdminPanel />
                    </RequireAuth>
                  } 
                />
                <Route 
                  path="/admin/category-order" 
                  element={
                    <RequireAuth>
                      <CategoryOrder />
                    </RequireAuth>
                  } 
                />
              </Routes>
              <Footer />
            </div>
          </CategoryOrderProvider>
        </CategoryProvider>
      </AuthProvider>
    </Router>
  );
}

export default App;