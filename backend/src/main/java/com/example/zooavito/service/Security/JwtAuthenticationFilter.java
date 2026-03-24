package com.example.zooavito.service.Security;

import com.example.zooavito.model.User;
import com.example.zooavito.repository.UserRepository;
import com.example.zooavito.service.User.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository; // Добавляем репозиторий

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();
        if (path.startsWith("/swagger-ui") ||
    path.startsWith("/v3/api-docs") ||
    path.startsWith("/v1/api/auth") ||
    path.startsWith("/v1/api/registration") ||
    (path.startsWith("/v1/api/announcement") && !path.contains("/user/") && request.getMethod().equals("GET")) ||
    (path.startsWith("/v1/api/categories") && request.getMethod().equals("GET")) ||
    (path.startsWith("/v1/api/subcategories") && request.getMethod().equals("GET"))) {
    filterChain.doFilter(request, response);
    return;
}

        String token = getJwtFromRequest(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsername(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            User user = userRepository.findByEmail(username);
            if (user == null || !user.isEnabled()) {
                log.warn("Заблокированный пользователь {} пытается выполнить запрос: {}", username, request.getRequestURI());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Ваш аккаунт заблокирован\"}");
                return;
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}