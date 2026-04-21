package com.baserbac.security;

import com.baserbac.annotation.RequirePermission;
import com.baserbac.common.constant.CommonConstant;
import com.baserbac.common.constant.RedisKeyConstant;
import com.baserbac.common.enums.ResultCode;
import com.baserbac.common.exception.BusinessException;
import com.baserbac.common.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 权限拦截器
 * 校验用户是否拥有访问接口所需的权限
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        log.info("=== PermissionInterceptor 开始处理 ===");
        log.info("请求路径: {}", requestURI);
        
        // 只处理方法级别的请求
        if (!(handler instanceof HandlerMethod)) {
            log.info("非方法级请求，直接放行");
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        
        // 获取方法上的权限注解
        RequirePermission requirePermission = handlerMethod.getMethodAnnotation(RequirePermission.class);
        if (requirePermission == null) {
            log.info("没有 @RequirePermission 注解，只需登录即可访问");
            // 检查是否已登录
            Long userId = SecurityUtil.getCurrentUserId();
            log.info("当前用户ID: {}", userId);
            if (userId == null) {
                log.error("=== 问题发现: 未登录（SecurityContext中没有认证信息） ===");
                log.error("可能原因: 1. Token无效 2. Token比对失败 3. JWT验证失败");
                throw new BusinessException(ResultCode.UNAUTHORIZED);
            }
            log.info("用户已登录，userId: {}", userId);
            return true;
        }

        // 获取所需权限标识
        String requiredPermission = requirePermission.value();
        log.info("需要权限: {}", requiredPermission);
        
        // 获取当前用户ID
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("当前用户ID: {}", userId);
        
        if (userId == null) {
            log.error("=== 问题发现: 未登录 ===");
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        // 超级管理员拥有所有权限，直接放行
        if (CommonConstant.SUPER_ADMIN_USER_ID.equals(userId)) {
            log.info("超级管理员，直接放行");
            return true;
        }
        
        // 从 Redis获取用户权限集合
        String permsKey = RedisKeyConstant.PERMISSIONS_PREFIX + userId;
        log.info("Redis权限Key: {}", permsKey);
        
        Set<Object> userPermissionsObj = redisTemplate.opsForSet().members(permsKey);
        log.info("Redis中的权限: {}", userPermissionsObj);
                
        // 如果缓存中没有权限数据，说明需要重新加载（可能是缓存过期或被清除）
        if (userPermissionsObj == null || userPermissionsObj.isEmpty()) {
            log.warn("用户权限缓存为空，userId: {}", userId);
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
                
        // 转换为String集合
        Set<String> userPermissions = userPermissionsObj.stream()
                .map(Object::toString)
                .collect(java.util.stream.Collectors.toSet());

        // 校验权限
        if (!userPermissions.contains(requiredPermission)) {
            log.warn("用户无权限访问，userId: {}, 所需权限: {}", userId, requiredPermission);
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        log.info("=== 权限校验通过 ===");
        log.info("userId: {}, 权限: {}", userId, requiredPermission);
        return true;
    }
}
