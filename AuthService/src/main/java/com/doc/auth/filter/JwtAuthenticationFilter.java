package com.doc.auth.filter;

import com.doc.auth.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import com.doc.auth.service.TokenBlacklistService;

/**
 * 一个自定义的Spring Security过滤器，用于处理JWT认证。
 * 在SecurityConfig中实现了构造参数的传入
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final String tokenPrefix;
    private final String headerAuth;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, 
                                 String tokenPrefix, 
                                 String headerAuth,
                                 TokenBlacklistService tokenBlacklistService) {
        this.jwtUtils = jwtUtils;
        this.tokenPrefix = tokenPrefix;
        this.headerAuth = headerAuth;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    /**
     * 过滤器的核心逻辑，在每个请求到达控制器之前执行。
     *
     * @param request       当前的HTTP请求对象
     * @param response      当前的HTTP响应对象
     * @param filterChain   过滤器链，用于继续处理请求
     * @throws ServletException 如果过滤器遇到问题
     * @throws IOException      如果I/O错误发生
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);

            if (jwt != null) {
                // 检查令牌是否在黑名单中
                if (tokenBlacklistService.isBlacklisted(jwt, "ACCESS")) {
                    SecurityContextHolder.clearContext();
                } else {
                    Claims claims = jwtUtils.validateToken(jwt);
                    if (!jwtUtils.isRefreshToken(claims)) {  // 只处理访问令牌
                        String newAccessToken = jwtUtils.updateAccessToken(claims);

                        if (newAccessToken == null) {
                            // 如果超过12小时没有活动，清除认证状态
                            SecurityContextHolder.clearContext();

                            // 后续可以添加重定向
                        } else {
                            // 设置新的访问令牌到响应头
                            response.setHeader(headerAuth, tokenPrefix + newAccessToken);

                            // 更新认证状态
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(claims.getSubject(), null, null);
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("无法设置用户认证: {}", e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(headerAuth);
        if (bearerToken != null && bearerToken.startsWith(tokenPrefix)) {
            return bearerToken.substring(tokenPrefix.length());
        }
        return null;
    }
} 