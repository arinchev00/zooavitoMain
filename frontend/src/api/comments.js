import api from './axios';

export const getCommentsByAnnouncement = (announcementId) => 
  api.get(`/comments/announcement/${announcementId}`);

export const createComment = (announcementId, data) => 
  api.post(`/comments/announcement/${announcementId}`, data);

export const updateComment = (id, data) => 
  api.put(`/comments/${id}`, data);

export const deleteComment = (id) => 
  api.delete(`/comments/${id}`);