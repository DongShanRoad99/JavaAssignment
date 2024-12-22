package com.doc.auth.service.impl;

import com.doc.auth.mapper.UserMapper;
import com.doc.auth.model.AuthPayload;
import com.doc.auth.model.User;
import com.doc.auth.model.input.LoginInput;
import com.doc.auth.model.input.RegisterInput;
import com.doc.auth.service.AuthService;
import com.doc.auth.service.TokenBlacklistService;
import com.doc.auth.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletResponse response;
    private final String refreshTokenCookie;
    private final String headerAuth;
    private final String tokenPrefix;
    private final TokenBlacklistService tokenBlacklistService;
    private final HttpServletRequest request;

    /**
     * 构造函数依赖注入所有必要的组件和服务。
     *
     * @param userMapper         用户映射器
     * @param jwtUtils           JWT工具类
     * @param passwordEncoder    密码编码器
     * @param response           HTTP响应对象
     * @param refreshTokenCookie 刷新令牌的Cookie名称
     * @param headerAuth         HTTP头中的认证头名称
     * @param tokenPrefix        HTTP头中的令牌前缀
     * @param tokenBlacklistService 令牌黑名单服务
     * @param request            HTTP请求对象
     */
    @Autowired
    public AuthServiceImpl(UserMapper userMapper,
                         JwtUtils jwtUtils,
                         PasswordEncoder passwordEncoder,
                         HttpServletResponse response,
                         @Value("${security.refresh-token-cookie}") String refreshTokenCookie,
                         @Value("${security.header-auth}") String headerAuth,
                         @Value("${security.token-prefix}") String tokenPrefix,
                         TokenBlacklistService tokenBlacklistService,
                         HttpServletRequest request) {
        this.userMapper = userMapper;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.response = response;
        this.refreshTokenCookie = refreshTokenCookie;
        this.headerAuth = headerAuth;
        this.tokenPrefix = tokenPrefix;
        this.tokenBlacklistService = tokenBlacklistService;
        this.request = request;
    }

    /**
     * 获取当前认证用户的信息。
     *
     * @return 当前用户的User实体
     */
    @Override
    public User getCurrentUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userMapper.selectById(Long.parseLong(userId));
    }

    @Override
    public AuthPayload login(LoginInput input) {
        User user = userMapper.selectByUsername(input.getUsername());
        if (user == null || !passwordEncoder.matches(input.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("用户名或密码错误");
        }
        
        return generateTokens(user);
    }

    @Override
    public AuthPayload register(RegisterInput input) {
        if (userMapper.selectByUsername(input.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(input.getUsername());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        
        userMapper.insert(user);
        
        return generateTokens(user);
    }

    private AuthPayload generateTokens(User user) {
        String accessToken = jwtUtils.generateAccessToken(user.getId().toString());
        String refreshToken = jwtUtils.generateRefreshToken(user.getId().toString());
        
        // 设置刷新令牌到HTTP-only cookie
        Cookie cookie = new Cookie(refreshTokenCookie, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // 启用HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(43200); // 12小时
        response.addCookie(cookie);
        
        // 设置初始访问令牌到响应头
        response.setHeader(headerAuth, tokenPrefix + accessToken);
        
        return new AuthPayload(accessToken, user);
    }

    @Override
    public Boolean logout() {
        try {
            // 获取访问令牌
            String accessToken = getJwtFromRequest();
            if (accessToken != null) {
                Claims accessClaims = jwtUtils.validateToken(accessToken);
                long accessTtl = jwtUtils.getTokenRemainingTime(accessClaims);
                if (accessTtl > 0) {
                    tokenBlacklistService.addToBlacklist(accessToken, "ACCESS", accessTtl);
                }
            }

            // 获取刷新令牌
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (refreshTokenCookie.equals(cookie.getName())) {
                        String refreshToken = cookie.getValue();
                        Claims refreshClaims = jwtUtils.validateToken(refreshToken);
                        long refreshTtl = jwtUtils.getTokenRemainingTime(refreshClaims);
                        if (refreshTtl > 0) {
                            tokenBlacklistService.addToBlacklist(refreshToken, "REFRESH", refreshTtl);
                        }
                        break;
                    }
                }
            }

            // 清除刷新令牌cookie
            Cookie cookie = new Cookie(refreshTokenCookie, "");
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String getJwtFromRequest() {
        String bearerToken = request.getHeader(headerAuth);
        if (bearerToken != null && bearerToken.startsWith(tokenPrefix)) {
            return bearerToken.substring(tokenPrefix.length());
        }
        return null;
    }
} 