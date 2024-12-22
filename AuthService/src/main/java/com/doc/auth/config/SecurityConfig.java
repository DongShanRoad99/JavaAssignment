package com.doc.auth.config;

import com.doc.auth.filter.JwtAuthenticationFilter;
import com.doc.auth.service.TokenBlacklistService;
import com.doc.auth.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtils jwtUtils;
    private final String tokenPrefix;
    private final String headerAuth;
    private final TokenBlacklistService tokenBlacklistService;

    public SecurityConfig(
            JwtUtils jwtUtils,
            @Value("${security.token-prefix}") String tokenPrefix,
            @Value("${security.header-auth}") String headerAuth,
            TokenBlacklistService tokenBlacklistService) {
        this.jwtUtils = jwtUtils;
        this.tokenPrefix = tokenPrefix;
        this.headerAuth = headerAuth;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    /**
     * 负责构建并返回一个SecurityFilterChain对象。
     * 这个Bean配置了HTTP安全设置，包括禁用CSRF保护、设置会话管理策略为无状态（stateless），
     * 并指定哪些URL路径可以被公开访问，哪些需要认证。
     *
     * @param http 提供了用于配置HTTP安全性的API。
     * @return 返回配置好的SecurityFilterChain实例。
     * @throws Exception 如果配置过程中发生错误，则抛出异常。
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())   // 禁用CSRF保护，适用于无状态API。
            .sessionManagement(session -> session   // 设置会话管理策略为无状态，适合JWT认证。
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/graphql").permitAll()    // 允许所有用户访问GraphQL端点。
                .requestMatchers("/graphiql").permitAll()   // 允许所有用户访问GraphiQL端点
                .anyRequest().authenticated()) // 其他请求需要认证。与SpringSecurity有关
            .addFilterBefore(jwtAuthenticationFilter(), 
                           UsernamePasswordAuthenticationFilter.class);
        
        return http.build();    // 构建并返回配置的HttpSecurity对象
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtils, tokenPrefix, headerAuth, tokenBlacklistService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 