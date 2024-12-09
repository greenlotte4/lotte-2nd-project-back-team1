package com.BackEndTeam1.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    private static  final String AUTH_HEADER = "Authorization";
    private static  final String TOKEN_PREFIX = "Bearer";

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain) throws ServletException, IOException {

        String header = req.getHeader(AUTH_HEADER);
//        log.info("여기1" + header);

        String token = null;
        if(header != null && header.startsWith(TOKEN_PREFIX)) {
            token = header.substring(TOKEN_PREFIX.length()).trim();
        }
//        log.info("여기2"+token);

        // 토큰 검사
        if(token != null) {

            try {
                jwtProvider.validateToken(token);

                Authentication authentication = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);


            } catch (Exception e) {
                // 토큰이 이상이 있으면 실패 응답
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(e.getMessage());
                log.info("here5 - " + e.getMessage());
                return; // 처리종료
            }
        }

        filterChain.doFilter(req, resp);
    }
}
