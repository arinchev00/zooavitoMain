package com.example.zooavito.service.Admin;

import com.example.zooavito.response.UserAdminResponse;
import java.util.List;

public interface AdminService {
    List<UserAdminResponse> getAllUsers();
    void blockUser(Long userId);
    void unblockUser(Long userId);
    void changeUserRole(Long userId, String role);
}