package com.baserbac.security;

import com.baserbac.common.constant.RedisKeyConstant;
import com.baserbac.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getTokenFromRequest(request);
            String requestURI = request.getRequestURI();
            
            log.info("=== 开始认证流程 ===");
            log.info("请求路径: {}", requestURI);
            log.info("请求中的Token: {}", token != null ? (token.substring(0, Math.min(50, token.length())) + "...") : "null");

            if (StringUtils.hasText(token)) {
                log.info("开始验证JWT Token...");
                boolean jwtValid = jwtUtil.validateToken(token);
                log.info("JWT验证结果: {}", jwtValid);
                
                if (jwtValid) {
                    String username = jwtUtil.getUsernameFromToken(token);
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    
                    log.info("JWT解析成功 - 用户名: {}, 用户ID: {}", username, userId);

                    String redisKey = RedisKeyConstant.TOKEN_PREFIX + userId;
                    log.info("Redis Key: {}", redisKey);
                    
                    String cachedToken = stringRedisTemplate.opsForValue().get(redisKey);
                    log.info("Redis中存储的Token: {}", cachedToken != null ? (cachedToken.substring(0, Math.min(50, cachedToken.length())) + "...") : "null");
                    log.info("请求中的Token: {}", token.substring(0, Math.min(50, token.length())) + "...");
                    
                    log.info("Token长度对比 - Redis: {}, 请求: {}", 
                             cachedToken != null ? cachedToken.length() : 0, 
                             token.length());
                    
                    log.info("Token完全一致对比: {}", cachedToken != null && cachedToken.equals(token));
                    
                    if (cachedToken == null) {
                        log.error("=== 问题发现: Redis中没有Token ===");
                        log.error("可能原因: 1. Token已过期 2. Redis数据被清除 3. 用户ID不正确");
                    } else if (!cachedToken.equals(token)) {
                        log.error("=== 问题发现: Token不匹配 ===");
                        log.error("可能原因: 1. 使用了旧的Token 2. 多次登录后Redis中的Token已更新");
                    }

                    boolean tokenValid = cachedToken != null && cachedToken.trim().equals(token.trim());

                    if (tokenValid) {
                        log.info("Token验证通过，开始加载用户详情...");
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        log.info("用户详情加载成功: {}", userDetails.getUsername());
                        log.info("用户权限: {}", userDetails.getAuthorities());

                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                            );
                        
                        authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        log.info("=== 认证成功 ===");
                        log.info("SecurityContext中的Authentication: {}", 
                                 SecurityContextHolder.getContext().getAuthentication() != null ? 
                                 SecurityContextHolder.getContext().getAuthentication().getName() : "null");
                    } else {
                        log.warn("=== Token验证失败 ===");
                        log.warn("用户: {}, 路径: {}", username, requestURI);
                        log.warn("Redis key: {}", redisKey);
                    }
                } else {
                    log.warn("=== JWT验证失败 ===");
                    log.warn("请求路径: {}", requestURI);
                }
            } else {
                log.info("=== 请求中没有Token ===");
                log.info("请求路径: {}", requestURI);
            }
        } catch (Exception e) {
            log.error("=== JWT认证处理出现异常 ===");
            log.error("异常信息: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
