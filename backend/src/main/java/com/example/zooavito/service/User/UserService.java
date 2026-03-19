package com.example.zooavito.service.User;

import com.example.zooavito.model.User;
import com.example.zooavito.request.UpdateUserRequest;
import com.example.zooavito.request.UserRegistrationRequest;
import com.example.zooavito.response.UpdateUserResponse;
import com.example.zooavito.response.UserRegistrationResponse;

public interface UserService {
    void save(User user);
    User findByEmail(String email);
    UserRegistrationResponse registerUser(UserRegistrationRequest request);
    UserRegistrationResponse buildUserResponse(User user);
    UpdateUserResponse updateUser(String email, UpdateUserRequest request);
}
