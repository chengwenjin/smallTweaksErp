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

            if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                Long userId = jwtUtil.getUserIdFromToken(token);

                log.debug("JWT验证通过，用户: {}, userId: {}, 请求路径: {}", username, userId, request.getRequestURI());

                String redisKey = RedisKeyConstant.TOKEN_PREFIX + userId;
                String cachedToken = stringRedisTemplate.opsForValue().get(redisKey);

                boolean tokenValid = cachedToken != null && cachedToken.equals(token);

                log.debug("Redis中token: {}", cachedToken);
                log.debug("请求中的token: {}", token);
                log.debug("Token验证结果: {}", tokenValid);

                if (tokenValid) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

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
                    
                    log.info("用户 {} 认证成功，请求路径: {}", username, request.getRequestURI());
                } else {
                    log.warn("Token已失效或不存在于Redis中，用户: {}, 路径: {}", username, request.getRequestURI());
                    log.warn("Redis key: {}", redisKey);
                }
            } else if (StringUtils.hasText(token)) {
                log.warn("JWT验证失败，请求路径: {}", request.getRequestURI());
            }
        } catch (Exception e) {
            log.error("JWT认证处理失败: {}", e.getMessage(), e);
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
