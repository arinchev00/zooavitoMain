package com.example.zooavito.response;

import com.example.zooavito.model.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSummaryResponse {
    private Long id;
    private String fullName;
    private String email;

    public static UserSummaryResponse from(User user) {
        if (user == null) return null;
        return UserSummaryResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
    }
}
