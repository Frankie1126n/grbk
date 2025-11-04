package com.blog.util;

import com.blog.config.JwtUtils;
import com.blog.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class UserTokenUtil {

    @Autowired
    private  JwtUtils jwtUtils;
    public  Integer getUserId(String token) {
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
        return jwtUtils.getUserIdFromToken(token);
    }
}
