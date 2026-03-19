import React, { useState, useRef, useCallback } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import ReCAPTCHA from "react-google-recaptcha"; // Другой импорт!
import { useAuth } from '../../context/AuthContext';
import { login } from '../../api/auth';
import './Auth.css';

const Login = () => {
  const navigate = useNavigate();
  const { login: authLogin } = useAuth();
  const captchaRef = useRef(null); // Создаем ref для капчи
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [captchaToken, setCaptchaToken] = useState(null); // Состояние для токена

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  // Функция вызывается при успешном прохождении капчи
  const onCaptchaChange = (token) => {
    console.log('reCAPTCHA token получен:', token);
    setCaptchaToken(token);
  };

  const handleSubmit = useCallback(async (e) => {
    e.preventDefault();
    setError('');

    // Получаем токен из ref (альтернативный способ)
    const token = captchaRef.current?.getValue();
    
    if (!token) {
      setError('Пожалуйста, подтвердите, что вы не робот');
      return;
    }

    setLoading(true);

    try {
      const response = await login({
        email: formData.email,
        password: formData.password,
        recaptchaToken: token
      });
      
      const { token: jwtToken, user } = response.data;
      
      authLogin(jwtToken, {
        id: user.id,
        fullName: user.fullName,
        phone: user.telephoneNumber,
        role: user.roles?.[0] || 'USER',
        email: user.email
      });
      
      navigate('/home');
    } catch (error) {
      console.error('Login error:', error);
      
      // Сбрасываем капчу при ошибке
      captchaRef.current?.reset();
      setCaptchaToken(null);
      
      if (error.response?.data) {
        if (error.response.data.recaptchaToken) {
          setError(error.response.data.recaptchaToken);
        } else if (error.response.data.account) {
          setError(error.response.data.account);
        } else if (error.response.data.message) {
          setError(error.response.data.message);
        } else {
          setError('Ошибка при входе. Проверьте email и пароль.');
        }
      } else {
        setError('Ошибка при входе. Попробуйте позже.');
      }
    } finally {
      setLoading(false);
    }
  }, [formData, authLogin, navigate]);

  return (
    <div className="container">
      <div className="row justify-content-center">
        <div className="col-md-5">
          <div className="auth-form-container">
            <h2 className="auth-title">Вход в систему</h2>
            
            {error && <div className="alert alert-danger">{error}</div>}
            
            <form onSubmit={handleSubmit} className="auth-form">
              <div className="form-group">
                <label htmlFor="email">Электронная почта</label>
                <input
                  type="email"
                  id="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  className="form-control"
                  placeholder="Введите электронную почту"
                  required
                  autoFocus
                />
              </div>

              <div className="form-group">
                <label htmlFor="password">Пароль</label>
                <input
                  type="password"
                  id="password"
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  className="form-control"
                  placeholder="Введите пароль"
                  required
                />
              </div>

              {/* ВИДИМАЯ КАПЧА */}
              <div className="form-group">
                <ReCAPTCHA
                  ref={captchaRef}
                  sitekey="6Ldnl40sAAAAANTuZ7EgfK42S21G4ZdbuPW0v4Qo" // Новый ключ!
                  onChange={onCaptchaChange}
                  theme="light"
                  size="normal"
                  hl="ru" // Русский язык
                />
              </div>

              <button 
                className="btn btn-primary btn-block mt-4" 
                type="submit"
                disabled={loading || !captchaToken}
              >
                {loading ? 'Вход...' : 'Войти'}
              </button>

              <div className="auth-footer">
                <p>Нет аккаунта? <Link to="/register">Зарегистрироваться</Link></p>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;