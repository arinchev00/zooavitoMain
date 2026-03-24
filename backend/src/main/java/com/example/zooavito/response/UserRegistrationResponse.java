package com.example.zooavito.response;

import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class UserRegistrationResponse {
    private Long id;
    private String fullName;
    private String email;
    private String telephoneNumber;
    private Set<String> roles;
}