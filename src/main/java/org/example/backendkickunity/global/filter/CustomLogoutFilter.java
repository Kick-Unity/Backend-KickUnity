package org.example.backendkickunity.global.filter;

import lombok.extern.slf4j.Slf4j;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.example.backendkickunity.global.util.JWTUtil;
import org.example.backendkickunity.member.repository.RefreshRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;


import java.io.IOException;

@Slf4j  // SLF4J 로깅을 사용
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public CustomLogoutFilter(JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // 요청 URI와 메서드 확인
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/api\\/logout$")) {
            log.debug("요청 URI가 /api/logout이 아닙니다.");
            filterChain.doFilter(request, response);
            return;
        }

        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            log.debug("요청 메서드가 POST가 아닙니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // refresh 토큰 가져오기
        String refresh = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.debug("Cookie name: {}, value: {}", cookie.getName(), cookie.getValue());
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                }
            }
        }

        // refresh 토큰이 없으면 400 응답
        if (refresh == null) {
            log.error("refresh 토큰이 없습니다.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // refresh 토큰 만료 확인
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            log.error("refresh 토큰이 만료되었습니다.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 토큰이 refresh 인지 확인
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            log.error("토큰이 refresh가 아닙니다.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // DB에 refresh 토큰 존재 여부 확인
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            log.error("DB에 해당 refresh 토큰이 존재하지 않습니다.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 로그아웃 처리: refresh 토큰 삭제
        refreshRepository.deleteByRefresh(refresh);

        // 쿠키에서 refresh 토큰 제거
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);  // 쿠키 삭제
        cookie.setPath("/");  // 쿠키 경로 설정
        cookie.setHttpOnly(true);  // 보안을 위한 HttpOnly 설정
        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
        log.debug("로그아웃 처리 완료.");
    }
}
