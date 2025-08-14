package com.example.board.security;

import com.example.board.security.auth.Role;
import lombok.Data;

@Data
public class MyProfileDto {
    private String email;
    private Role role;

    public MyProfileDto(String email, Role role) {
        this.email = email;
        this.role = role;
    }
}
