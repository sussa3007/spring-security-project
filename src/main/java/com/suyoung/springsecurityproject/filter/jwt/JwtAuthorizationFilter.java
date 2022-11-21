package com.suyoung.springsecurityproject.filter.jwt;

import com.suyoung.springsecurityproject.user.User;
import com.suyoung.springsecurityproject.user.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    public JwtAuthorizationFilter(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = null;
        try {
            /* 쿠키에서 토큰을 가져온다 */
            token = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals(JwtProperties.COOKIE_NAME))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
        } catch (Exception e) {

        }
        if (token != null) {
            try {
                Authentication authentication = getUsernamePasswordAuthenticationToken(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                Cookie cookie = new Cookie(JwtProperties.COOKIE_NAME, null);
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }
    /*
     * JWT 토큰으로 User를 찾아서 UsernamePasswordAuthenticationToken를 만들어서 반환한다.
     * User가 없다면 null
     */
    private Authentication getUsernamePasswordAuthenticationToken(String token) {
        String userName = JwtUtils.getUsername(token);
        if (userName != null) {
            User user = userRepository.findByUsername(userName);
            return new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities()
            );
        }
        return null;
    }
}
