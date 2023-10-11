package zone.yby.lab.filter;

import zone.yby.lab.domain.LoginUser;
import zone.yby.lab.utils.HashCode;
import zone.yby.lab.utils.JwtUtil;
import zone.yby.lab.utils.RedisCache;
import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final RedisCache redisCache;

    public JwtAuthenticationTokenFilter(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        //获取token
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token)) {
            // 没有的东西何必强求呢
            filterChain.doFilter(request, response);
            return;
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            // 少了一些东西，即使后面是对的，也没有必要继续了
            filterChain.doFilter(request, response);
            return;
        }
        // 但总有人是小调皮，不按套路出牌
        String Uid;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            Uid = claims.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("token非法或已过期");
        }
        // 有可能是小调皮，也有可能是小调皮的小调皮
        String sign = String.valueOf(HashCode.murmurhash(token + "cslg-lab-manager"));
        if (!redisCache.exists("token:" + sign)) {
            throw new RuntimeException("token非法或已过期");
        }
        // 现在可以尝试获取用户信息了
        LoginUser loginUser = redisCache.getCacheObject("login:" + Uid);
        // 也可能根本没有
        if (loginUser == null) {
            throw new RuntimeException("token非法或已过期");
        }
        // 太调皮的人会被禁止使用
        if (!loginUser.isEnabled()) {
            throw new RuntimeException("账号已经被禁用");
        }
        // 但有些人即使没有被禁止，也不会被允许使用
        if (!loginUser.isAccountNonExpired()) {
            throw new RuntimeException("账号已经过期");
        }
        // 存入SecurityContextHolder
        // 获取权限信息封装到Authentication中
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        // 放行
        filterChain.doFilter(request, response);
    }
}