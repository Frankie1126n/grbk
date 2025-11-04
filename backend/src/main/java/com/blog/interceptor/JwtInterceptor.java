package com.blog.interceptor;

import com.blog.config.JwtUtils;
import com.blog.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT Interceptor - JWT拦截器
 * 
 * @author Blog System
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // OPTIONS请求直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 从请求头获取Token
        String token = request.getHeader("Authorization");
        
        System.out.println("=== JWT Interceptor ===");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Authorization Header: " + token);
        
        if (StringUtils.isBlank(token)) {
            throw new BusinessException(401, "未登录，请先登录");
        }

        // 去除Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证Token
        if (!jwtUtils.validateToken(token)) {
            throw new BusinessException(401, "登录已过期，请重新登录");
        }

        // 将用户ID存入请求属性
        Integer userId = jwtUtils.getUserIdFromToken(token);
        request.setAttribute("userId", userId);
        
        System.out.println("User ID: " + userId);
        System.out.println("======================\n");

        return true;
    }
}
