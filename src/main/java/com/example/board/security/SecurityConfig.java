package com.example.board.security;

import com.example.board.security.auth.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/login", "/register").permitAll()
                .anyRequest().authenticated());

        http.formLogin((form) -> form
                .loginPage("/login")
                .defaultSuccessUrl("/posts",true)
                .permitAll());

        http.oauth2Login(oauth -> oauth
                .loginPage("/login")
                .userInfoEndpoint((userInfo) -> userInfo.userService(customOAuth2UserService))
                .defaultSuccessUrl("/posts"));

        http.logout((logout) -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID"));


        http.userDetailsService(userDetailsService);

        return http.build();
    }
}
