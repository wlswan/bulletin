package com.example.board.security.auth;

import com.example.board.dto.UserDto;
import com.example.board.security.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

//세션에는 엔티티를 넣지 말자 다 고쳐
public class PrincipalDetails implements OAuth2User, UserDetails {
    private final Long userId;
    private final String email;
    private final Role role;
    private final String password;    private final Map<String,Object> attributes;

    public PrincipalDetails(UserDto userDto) {
        this.userId = userDto.getId();
        this.email = userDto.getEmail();
        this.role = userDto.getRole();
        this.password = userDto.getPassword();
        attributes = null;
    }

    public PrincipalDetails(UserDto userDto, Map<String, Object> attributes) {
        this.userId = userDto.getId();
        this.email = userDto.getEmail();
        this.role = userDto.getRole();
        this.password = userDto.getPassword();
        this.attributes = attributes;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public String getName() {
        return "";
    }
}
