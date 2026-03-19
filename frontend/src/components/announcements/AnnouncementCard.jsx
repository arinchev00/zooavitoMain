import React from 'react';
import { Link } from 'react-router-dom';

const AnnouncementCard = ({ announcement }) => {
  const mainImage = announcement.images && announcement.images.length > 0 
    ? announcement.images.find(img => img.main === true) || announcement.images[0]
    : null;

  return (
    <Link to={`/announcements/${announcement.id}`} style={{ textDecoration: 'none', color: 'inherit' }}>
      <div className="ad-card">
        {mainImage ? (
          <img 
            src={`data:${mainImage.contentType};base64,${mainImage.base64Image}`} 
            alt={announcement.title}
            onError={(e) => {
              e.target.onerror = null;
              e.target.src = 'https://via.placeholder.com/300x200?text=No+Image';
            }}
          />
        ) : (
          <div style={{ 
            width: '100%', 
            height: '150px', 
            backgroundColor: '#f8f9fa', 
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'center',
            borderRadius: '8px',
            marginBottom: '10px'
          }}>
            <span style={{ color: '#6c757d' }}>Нет фото</span>
          </div>
        )}
        
        <h5>{announcement.title}</h5>
        <div className="price">{announcement.price.toLocaleString()} ₽</div>
        
        <div className="location" style={{ marginTop: '8px' }}>
          <i className="fas fa-user"></i> {announcement.user?.fullName || announcement.user?.email}
        </div>
        
        <div className="meta-info" style={{ marginTop: '5px', fontSize: '11px', color: '#6c757d' }}>
          <div>
            <i className="fas fa-calendar" style={{ marginRight: '5px' }}></i>
            {new Date(announcement.dateOfPublication).toLocaleDateString('ru-RU')}
          </div>
          <div style={{ marginTop: '2px' }}>
            <i className="fas fa-tag" style={{ marginRight: '5px' }}></i>
            {announcement.category?.title}
            {announcement.subcategory && ` / ${announcement.subcategory.title}`}
          </div>
        </div>
      </div>
    </Link>
  );
};

export default AnnouncementCard;