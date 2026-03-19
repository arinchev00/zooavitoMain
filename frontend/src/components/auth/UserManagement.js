import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { getAllUsers, blockUser, unblockUser, changeUserRole } from '../../api/admin';

const UserManagement = () => {
  const { user: currentUser, refreshUserData } = useAuth();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      setLoading(true);
      const response = await getAllUsers();
      setUsers(response.data);
    } catch (error) {
      console.error('Ошибка при загрузке пользователей:', error);
      setError('Не удалось загрузить список пользователей');
    } finally {
      setLoading(false);
    }
  };

  const handleBlockToggle = async (user) => {
    try {
      if (user.enabled) {
        await blockUser(user.id);
        setSuccess(`Пользователь ${user.email} заблокирован`);
      } else {
        await unblockUser(user.id);
        setSuccess(`Пользователь ${user.email} разблокирован`);
      }
      loadUsers();
      setTimeout(() => setSuccess(''), 3000);
    } catch (error) {
      console.error('Ошибка при изменении статуса пользователя:', error);
      setError('Не удалось изменить статус пользователя');
    }
  };

 const handleRoleChange = async (userId, newRole) => {
  try {
    console.log('🔄 ROLE CHANGE: starting for user', userId, 'new role', newRole);
    console.log('🔄 ROLE CHANGE: current user', currentUser);
    
    await changeUserRole(userId, newRole);
    console.log('🔄 ROLE CHANGE: API call successful');
    
    // Если изменили роль НЕ текущего пользователя - очищаем localStorage у этого пользователя?
    // Но мы не можем это сделать напрямую. Нужно, чтобы клиент сам обновлял данные.
    
    // Пока просто обновляем список и показываем сообщение
    setSuccess('Роль пользователя успешно изменена');
    loadUsers();
    setTimeout(() => setSuccess(''), 3000);
  } catch (error) {
    console.error('❌ ROLE CHANGE: error', error);
    setError('Не удалось изменить роль пользователя');
  }
};

  const formatPhone = (phone) => {
    if (!phone) return '—';
    return phone;
  };

  const isCurrentUser = (userId) => {
    return currentUser?.id === userId;
  };

  if (loading) {
    return (
      <div className="text-center py-4">
        <div className="spinner-border text-success" role="status">
          <span className="sr-only">Загрузка...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="user-management">
      {error && <div className="alert alert-danger">{error}</div>}
      {success && <div className="alert alert-success">{success}</div>}

      <div className="table-responsive">
        <table className="table table-striped table-hover">
          <thead>
            <tr>
              <th>ID</th>
              <th>Имя</th>
              <th>Email</th>
              <th>Телефон</th>
              <th>Роль</th>
              <th>Статус</th>
              <th>Действия</th>
            </tr>
          </thead>
          <tbody>
            {users.map(user => {
              const cannotModify = isCurrentUser(user.id);
              
              return (
                <tr key={user.id} className={cannotModify ? 'table-info' : ''}>
                  <td>{user.id} {cannotModify && <span className="badge badge-info">(это вы)</span>}</td>
                  <td>{user.fullName || '—'}</td>
                  <td>{user.email}</td>
                  <td>{formatPhone(user.telephoneNumber)}</td>
                  <td>
                    <select
                      className="form-control form-control-sm"
                      value={user.roles?.[0] || 'ROLE_USER'}
                      onChange={(e) => handleRoleChange(user.id, e.target.value)}
                      disabled={cannotModify}
                      style={{ width: '150px' }}
                    >
                      <option value="ROLE_USER">Пользователь</option>
                      <option value="ROLE_ADMIN">Администратор</option>
                    </select>
                  </td>
                  <td>
                    <span className={`badge ${user.enabled ? 'badge-success' : 'badge-danger'}`}>
                      {user.enabled ? 'Активен' : 'Заблокирован'}
                    </span>
                  </td>
                  <td>
                    <button
                      className={`btn btn-sm ${user.enabled ? 'btn-warning' : 'btn-success'}`}
                      onClick={() => handleBlockToggle(user)}
                      disabled={cannotModify}
                    >
                      {user.enabled ? 'Заблокировать' : 'Разблокировать'}
                    </button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default UserManagement;