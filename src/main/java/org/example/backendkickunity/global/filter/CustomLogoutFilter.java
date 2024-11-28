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


@Slf4j
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public CustomLogoutFilter(JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 요청 URI 와 메서드 확인
        log.debug("POST 요청 받음 - URI: {}", ((HttpServletRequest) request).getRequestURI());  // 요청 URI 로그
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // 요청 URI 확인
        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();

        log.debug("요청 URI: {}", requestUri);  // 요청 URI 출력

        log.debug("요청 URI: {}, 요청 메서드: {}", requestUri, requestMethod);  // URI와 메서드 출력

        // 로그아웃 경로가 맞는지 확인
        if (requestUri.equals("/api/logout") && requestMethod.equals("POST")) {
            // 로그아웃 처리
            log.debug("로그아웃 경로로 요청이 왔습니다. 필터 진행.");
            filterChain.doFilter(request, response);  // 필터 체인 진행
        } else {
            log.debug("로그아웃 경로가 아니거나 POST 요청이 아닙니다. 필터 통과.");
            filterChain.doFilter(request, response);  // 필터 통과
        }

        if (!requestUri.matches("^/api/logout/?$")) {
            log.debug("로그아웃 URI가 아닙니다. 필터 통과");  // 로그아웃 URI가 아닐 경우
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("요청 메서드: {}", requestMethod);  // 요청 메서드 출력
        if (!requestMethod.equals("POST")) {
            log.debug("POST 메서드가 아닙니다. 필터 통과");  // POST 메서드가 아니면 필터 통과
            filterChain.doFilter(request, response);
            return;
        }

        // refresh 토큰 가져오기
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.debug("쿠키 이름: {}, 값: {}", cookie.getName(), cookie.getValue());  // 쿠키 이름과 값 출력
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                }
            }
        }


        // refresh 토큰이 없으면 400 응답
        if (refresh == null) {
            log.error("refresh 토큰이 없습니다.");  // refresh 토큰이 없으면 오류 로그
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // refresh 토큰 만료 확인
        try {
            jwtUtil.isExpired(refresh);
            log.debug("refresh 토큰 만료되지 않음: {}", refresh);  // 만료되지 않은 토큰 확인 로그
        } catch (ExpiredJwtException e) {
            log.error("refresh 토큰이 만료되었습니다: {}", refresh);  // 만료된 토큰 처리
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 토큰이 refresh 인지 확인
        String category = jwtUtil.getCategory(refresh);
        log.debug("refresh 토큰의 카테고리: {}", category);  // 토큰 카테고리 확인
        if (!category.equals("refresh")) {
            log.error("refresh 토큰이 아닙니다. 카테고리: {}", category);  // refresh 토큰이 아니면 오류 로그
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // DB에서 refresh 토큰 존재 여부 확인
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        log.debug("DB에 해당 refresh 토큰 존재 여부: {}", isExist);  // DB에서 토큰 존재 여부 출력
        if (!isExist) {
            log.error("DB에 해당 refresh 토큰이 존재하지 않습니다: {}", refresh);  // DB에 토큰이 존재하지 않으면 오류 로그
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 로그아웃 처리: DB에서 refresh 토큰 삭제
        refreshRepository.deleteByRefresh(refresh);
        log.debug("DB에서 refresh 토큰 삭제됨: {}", refresh);  // DB에서 토큰 삭제 로그

        // 쿠키에서 refresh 토큰 제거
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);  // 쿠키 삭제
        cookie.setPath("/");  // 쿠키 경로 설정
        cookie.setHttpOnly(true);  // 보안을 위한 HttpOnly 설정
        response.addCookie(cookie);
        log.debug("쿠키에서 refresh 토큰 삭제됨");  // 쿠키에서 토큰 삭제 로그

        // 정상적인 로그아웃 처리 후 200 OK 응답
        response.setStatus(HttpServletResponse.SC_OK);
        log.debug("로그아웃 처리 완료");  // 로그아웃 완료 로그
    }
}
