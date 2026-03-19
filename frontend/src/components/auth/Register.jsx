import React, { useState, useRef } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import ReCAPTCHA from "react-google-recaptcha";
import { register } from '../../api/auth';
import './Auth.css';

const Register = () => {
  const navigate = useNavigate();
  const captchaRef = useRef(null);
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    telephoneNumber: '',
    password: '',
    confirmPassword: ''
  });
  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState('');
  const [loading, setLoading] = useState(false);
  const [captchaToken, setCaptchaToken] = useState(null); // 👈 Добавлено

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const formatPhoneNumber = (value) => {
    let numbers = value.replace(/\D/g, '');
    
    if (numbers.length > 0) {
      if (numbers[0] === '8' || numbers[0] === '7') {
        numbers = '7' + numbers.substring(1);
      }
      
      let formatted = '+7';
      
      if (numbers.length > 1) {
        formatted += '(' + numbers.substring(1, 4);
      }
      if (numbers.length >= 5) {
        formatted += ')' + numbers.substring(4, 7);
      }
      if (numbers.length >= 8) {
        formatted += '-' + numbers.substring(7, 9);
      }
      if (numbers.length >= 10) {
        formatted += '-' + numbers.substring(9, 11);
      }
      
      return formatted;
    }
    return value;
  };

  const handlePhoneChange = (e) => {
    const formatted = formatPhoneNumber(e.target.value);
    setFormData({ ...formData, telephoneNumber: formatted });
  };

  const onCaptchaChange = (token) => {
    setCaptchaToken(token);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors({});
    setServerError('');

    // 👇 ТЕПЕРЬ ПРОВЕРКА ВНУТРИ ФУНКЦИИ - ВСЁ ПРАВИЛЬНО
    if (!captchaToken) {
      setServerError('Пожалуйста, подтвердите, что вы не робот');
      return;
    }

    setLoading(true);

    try {
      await register({
        fullName: formData.fullName,
        email: formData.email,
        telephoneNumber: formData.telephoneNumber,
        password: formData.password,
        confirmPassword: formData.confirmPassword,
        recaptchaToken: captchaToken
      });
      
      navigate('/login', { state: { message: 'Регистрация успешна! Теперь вы можете войти.' } });
    } catch (error) {
      captchaRef.current?.reset();
      setCaptchaToken(null);
      
      if (error.response?.data) {
        if (error.response.data.recaptchaToken) {
          setServerError(error.response.data.recaptchaToken);
        } else {
          setErrors(error.response.data);
        }
      } else {
        setServerError('Ошибка при регистрации. Попробуйте позже.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className="auth-form-container">
            <h2 className="auth-title">Регистрация нового пользователя</h2>
            <p className="auth-subtitle">Заполните форму ниже для создания учетной записи</p>
            
            {serverError && <div className="alert alert-danger">{serverError}</div>}
            
            <form onSubmit={handleSubmit} className="auth-form">
              <div className="form-group">
                <label htmlFor="fullName">Ваше имя</label>
                <input
                  type="text"
                  id="fullName"
                  name="fullName"
                  value={formData.fullName}
                  onChange={handleChange}
                  className={`form-control ${errors.fullName ? 'is-invalid' : ''}`}
                  placeholder="Введите имя"
                  required
                />
                {errors.fullName && <div className="invalid-feedback">{errors.fullName}</div>}
              </div>

              <div className="form-group">
                <label htmlFor="email">Электронная почта</label>
                <input
                  type="email"
                  id="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  className={`form-control ${errors.email ? 'is-invalid' : ''}`}
                  placeholder="Введите электронную почту"
                  required
                />
                {errors.email && <div className="invalid-feedback">{errors.email}</div>}
              </div>

              <div className="form-group">
                <label htmlFor="telephoneNumber">Номер телефона (необязательно)</label>
                <div className="input-group">
                  <div className="input-group-prepend">
                    <span className="input-group-text"><i className="fas fa-phone"></i></span>
                  </div>
                  <input
                    type="tel"
                    id="telephoneNumber"
                    name="telephoneNumber"
                    value={formData.telephoneNumber}
                    onChange={handlePhoneChange}
                    className={`form-control ${errors.telephoneNumber ? 'is-invalid' : ''}`}
                    placeholder="+7 (999) 123-45-67"
                    maxLength="18"
                  />
                </div>
                <small className="form-text text-muted">Формат: +7 (999) 123-45-67</small>
                {errors.telephoneNumber && <div className="invalid-feedback">{errors.telephoneNumber}</div>}
              </div>

              <div className="form-group">
                <label htmlFor="password">Пароль</label>
                <input
                  type="password"
                  id="password"
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  className={`form-control ${errors.password ? 'is-invalid' : ''}`}
                  placeholder="Введите пароль"
                  required
                />
                {errors.password && <div className="invalid-feedback">{errors.password}</div>}
                <small className="form-text text-muted">Минимальная длина пароля - 6 символов</small>
              </div>

              <div className="form-group">
                <label htmlFor="confirmPassword">Подтверждение пароля</label>
                <input
                  type="password"
                  id="confirmPassword"
                  name="confirmPassword"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  className={`form-control ${errors.confirmPassword ? 'is-invalid' : ''}`}
                  placeholder="Подтвердите пароль"
                  required
                />
                {errors.confirmPassword && <div className="invalid-feedback">{errors.confirmPassword}</div>}
                <small className="form-text text-muted">Пароль должен совпадать с введенным ранее значением</small>
              </div>

              {/* 👇 ВИДИМАЯ КАПЧА */}
              <div className="form-group">
                <ReCAPTCHA
                  ref={captchaRef}
                  sitekey="6Ldnl40sAAAAANTuZ7EgfK42S21G4ZdbuPW0v4Qo"
                  onChange={onCaptchaChange}
                  theme="light"
                  size="normal"
                  hl="ru"
                />
              </div>

              <button 
                className="btn btn-primary btn-block mt-4" 
                type="submit"
                disabled={loading || !captchaToken}
              >
                {loading ? 'Регистрация...' : 'Зарегистрироваться'}
              </button>

              <div className="auth-footer">
                <p>Уже есть аккаунт? <Link to="/login">Войдите в систему</Link></p>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Register;