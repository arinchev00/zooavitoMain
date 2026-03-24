// components/common/ErrorMessage.jsx
import React from 'react';

const ErrorMessage = ({ errors, onClose }) => {
  if (!errors || errors.length === 0) return null;

  const messages = Array.isArray(errors) ? errors : [errors];

  return (
    <div className="alert alert-danger alert-dismissible fade show" role="alert">
      <strong>Ошибка загрузки файлов:</strong>
      <ul className="mb-0 mt-2">
        {messages.map((msg, idx) => (
          <li key={idx}>{msg}</li>
        ))}
      </ul>
      {onClose && (
        <button type="button" className="close" onClick={onClose}>
          <span>&times;</span>
        </button>
      )}
    </div>
  );
};

export default ErrorMessage;