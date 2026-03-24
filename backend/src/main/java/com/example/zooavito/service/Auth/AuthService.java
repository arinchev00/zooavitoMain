package com.example.zooavito.service.Auth;

import com.example.zooavito.request.AuthRequest;
import com.example.zooavito.response.AuthResponse;

public interface AuthService {
    AuthResponse authenticateUser(AuthRequest authRequest);
}
