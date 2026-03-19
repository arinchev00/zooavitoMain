package com.example.zooavito.response;

import com.example.zooavito.model.Role;
import com.example.zooavito.model.User;
import lombok.Builder;
import lombok.Data;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
public class UserAdminResponse {
    private Long id;
    private String email;
    private String fullName;
    private String telephoneNumber;
    private Set<String> roles;
    private boolean enabled;

    public static UserAdminResponse from(User user) {
        Set<String> roleTitles = user.getRoles().stream()
                .map(Role::getTitle)
                .collect(Collectors.toSet());

        return UserAdminResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .telephoneNumber(user.getTelephoneNumber())
                .roles(roleTitles)
                .enabled(user.isEnabled())
                .build();
    }
}